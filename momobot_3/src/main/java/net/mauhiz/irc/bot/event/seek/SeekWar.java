package net.mauhiz.irc.bot.event.seek;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mauhiz.irc.MomoStringUtils;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.util.NetUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

/**
 * @author Topper
 */
public class SeekWar {
    /**
     * pattern de detection d une IP
     */
    private static final Pattern IP_PATTERN = Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3}\\:\\d{1,5}");
    /**
     * logger.
     */
    private static final Logger LOG = Logger.getLogger(SeekWar.class);
    /**
     * port maxi
     */
    private static final int MAX_SRV_PORT = Character.MAX_VALUE;
    /**
     * port mini
     */
    private static final int MIN_SRV_PORT = 1024;
    /**
     * liste des channels de seek
     */
    public static final Collection<String> SEEK_CHANS = Arrays.asList("#clanwar.fr");
    /**
     * 
     */
    static final Collection<String> SEPARATEUR = Arrays.asList("vs", "v", "on", "o", "x");
    /**
     * black list pour un msg pv
     */
    private final Collection<String> blackList = Arrays.asList("www", "http", "://", ".com", ".fr", ".eu", ".info",
            "porn", "sex");
    /**
     * channel sur lequel le seek a ete lance
     */
    private IrcChannel channel;
    /**
     * 
     */
    private final Collection<String> greenList = Arrays.asList("ok", "oui", "go", "k", "ip", "pass", "yes", "lvl",
            "level", "yep", "moi");
    /**
     * IpPass du srv
     */
    private String ippass;
    /**
     * 
     */
    public boolean isLaunchedAndQuit;
    /**
     * Liste d'ordre croissante des lvl
     */
    private final List<String> lvl = Arrays.asList("noob", "low", "mid", "good", "skilled", "high", "roxor");
    /**
     * gather
     */
    private int numberPlayers = 5;
    /**
     * True si le seek est en cour ; false sinon
     */
    private boolean seekInProgress;
    /**
     * level seek
     */
    private String seekLevel;
    /**
     * Message de seek %P=nb Player ; %S=Serv ON/OFF ; %L = level
     */
    private String seekMessage;
    /**
     * si le gather a un serv de jeu ou pass
     */
    private String seekServ;
    /**
     * Temps de time out : 6min par defaut
     */
    private final long seekTimeOut = 6 * 60 * 1_000;
    /**
     * si on a splite
     */
    private boolean split;

    /**
     * le temps ou je commence.
     */
    private final StopWatch sw = new StopWatch();

    /**
     * 
     */
    private boolean sWarning;
    /**
     * Liste des users qui ont pv le bot
     */
    private final List<IrcUser> userpv = new ArrayList<>();

    public SeekWar() {
        super();
        seekServ = "ON";
        ippass = "87.98.196.75:27019 Gotserv.com: pw:gruik";
        seekLevel = "mid";
        seekMessage = "seek %Pv%P - %S - %L pm ";
    }

    /**
     * @return String = channel qui a lance le seek ~ #tsi.fr
     */
    public IrcChannel getChannel() {
        return channel;
    }

    /**
     * @return Message de seek complete
     */
    public String getMessageForSeeking() {
        return MomoStringUtils.genereSeekMessage(seekMessage, numberPlayers, seekServ, seekLevel);
    }

    /**
     * @return String
     * 
     */
    private String getSeekInfo() {
        return "Seek - Info : " + numberPlayers + "vs" + numberPlayers + " serv = " + seekServ + " level = "
                + seekLevel;
    }

    /**
     * @return String = qui a gagner le seek
     */

    public String getSeekWinner() {
        if (userpv.isEmpty()) {
            return "";
        }
        return userpv.get(0).getNick();
    }

    /**
     * @return String
     */
    public boolean isSeekInProgress() {
        return seekInProgress;
    }

    /**
     * @return false = TJS en vie true = DEAD!
     */
    public boolean isTimeOut() {
        LOG.debug("Time seek : " + sw.getTime());
        return sw.getTime() >= seekTimeOut;
    }

    /**
     * @param im
     * @param resultPrivmsg
     * @return list of {@link Privmsg}
     */
    private List<Privmsg> processIncomingMessage(Privmsg im, List<Privmsg> resultPrivmsg) {
        IrcUser provenance = (IrcUser) im.getFrom();

        String msg = im.getMessage();
        // Traitement des messages entrant
        if (!im.isToChannel()) {
            // C'est un msg PV

            // Si c'est "S" on se calme!
            if ("S".equalsIgnoreCase(provenance.getNick())) {
                sWarning = true;
                Privmsg msg1 = new Privmsg(im.getServerPeer(), null, channel, "S vient me faire ch**r. J'y vais calmos");
                resultPrivmsg.add(msg1);
            }

            // On continue de traiter le message PV
            if ("on".equals(seekServ.toLowerCase(Locale.FRENCH))) {

                if (userpv.contains(provenance)) {
                    // Le bot a deja ete PV par ce type
                    if (submitPVMessage(msg, provenance)) {
                        // OK le bot valide le pv <=> SEEK REUSSI
                        // On lui file ip pass
                        // + GOGOGO
                        if (msg.toLowerCase(Locale.FRENCH).contains("lvl")
                                || msg.toLowerCase(Locale.FRENCH).contains("level")) {

                            Privmsg msg1 = new Privmsg(im, seekLevel, true);
                            resultPrivmsg.add(msg1);
                            Privmsg msg2 = new Privmsg(im, "go?", true);
                            resultPrivmsg.add(msg2);

                        }
                        Privmsg msg1 = new Privmsg(im, ippass, true);
                        resultPrivmsg.add(msg1);
                        Privmsg msg2 = new Privmsg(im, "GOGOGO", true);
                        resultPrivmsg.add(msg2);
                        Privmsg msg3 = new Privmsg(im.getServerPeer(), null, channel, provenance.getNick()
                                + " a mordu! GOGOGO o//");
                        resultPrivmsg.add(msg3);

                        seekInProgress = false;
                        userpv.clear();
                        userpv.add(provenance);
                        split = false;
                        sw.stop();
                        sw.reset();
                        return resultPrivmsg;
                    }
                    // REFOULE
                    return resultPrivmsg;

                }
                // Le bot est PV pour la premiere fois >>
                // On le slap
                // Ensuite On lui demande si il est RDY
                userpv.add(provenance);
                Privmsg msg1 = new Privmsg(im, provenance.getNick(), true);
                resultPrivmsg.add(msg1);
                Privmsg msg2 = new Privmsg(im, "lvl?", true);
                resultPrivmsg.add(msg2);
                return resultPrivmsg;

            } else if ("off".equalsIgnoreCase(seekServ)) {
                // Le bot a deja ete PV par ce bonhomme
                if (userpv.contains(provenance)) {
                    if (msg.toLowerCase(Locale.FRENCH).contains("lvl")
                            || msg.toLowerCase(Locale.FRENCH).contains("level")) {
                        Privmsg msg1 = new Privmsg(im, seekLevel, true);
                        resultPrivmsg.add(msg1);
                        Privmsg msg2 = new Privmsg(im, "go?", true);
                        resultPrivmsg.add(msg2);
                    }

                    Privmsg msg1 = new Privmsg(im.getServerPeer(), null, channel, provenance.getNick() + " :" + msg);
                    resultPrivmsg.add(msg1);
                    return resultPrivmsg;
                }
                // Le bot est PV pour la premiere fois
                if (submitPVMessage(msg, provenance)) {
                    // Le bot detecte un msg correct
                    // On PV le bonhomme ok > GO
                    // On affiche le msg (cad l'ip & pass) ds le channel de seek
                    userpv.add(provenance);
                    if (msg.toLowerCase(Locale.FRENCH).contains("lvl")
                            || msg.toLowerCase(Locale.FRENCH).contains("level")) {
                        Privmsg msg1 = new Privmsg(im, seekLevel, true);
                        resultPrivmsg.add(msg1);
                        Privmsg msg2 = new Privmsg(im, "go?", true);
                        resultPrivmsg.add(msg2);

                    } else {
                        Privmsg msg1 = new Privmsg(im, "ok", true);
                        resultPrivmsg.add(msg1);
                        Privmsg msg2 = new Privmsg(im, "go!", true);
                        resultPrivmsg.add(msg2);
                        Privmsg msg3 = new Privmsg(im.getServerPeer(), null, channel, provenance.getNick() + " :" + msg);
                        resultPrivmsg.add(msg3);
                    }

                    return resultPrivmsg;

                }
                return resultPrivmsg;
            } else {
                // ERREUR INCONNU
                return resultPrivmsg;
            }

        }
        // C'est un msg d'un Channel
        if (submitChannelMessage(msg)) {

            if (userpv.contains(provenance)) {
                // Cas chelou :O ??!??!!??
                Privmsg msg1 = new Privmsg(im, "go?", true);
                resultPrivmsg.add(msg1);
                return resultPrivmsg;
            }
            // On pv le mec pr lui dire rdy?
            Privmsg msg1 = new Privmsg(im, "rdy?", true);
            resultPrivmsg.add(msg1);
            if ("off".equalsIgnoreCase(seekServ)) {
                Privmsg msg2 = new Privmsg(im, "ip&pass?", true);
                resultPrivmsg.add(msg2);
            }
            return resultPrivmsg;

        }

        return resultPrivmsg;
    }

    /**
     * @param cmd
     *            Collection<String> non normalise
     * @return un message
     */
    List<String> split(Collection<String> cmd) {
        List<String> cmdNormalise = new ArrayList<>();
        String tmpStg = "";

        boolean inquote = false;
        for (String element : cmd) {

            /* hey, xoring! */
            if (element.startsWith("\"") ^ element.endsWith("\"")) {
                inquote = !inquote;
            }

            if (tmpStg.length() == 0) {
                tmpStg = element;
            } else {
                tmpStg += " " + element;
            }
            if (!inquote) {
                cmdNormalise.add(tmpStg);
                tmpStg = "";
            }
        }
        return cmdNormalise;
    }

    /**
     * @param args
     *            1) RIEN 2) ON IPPASS LVL 3) OFF LVL 4) ON IPPASS LVL MSGSEEK 5) OFF LVL MSGSEEK
     * @param chan
     * @param nbPlayers
     * @return String
     */
    public String start(ArgumentList args, IrcChannel chan, int nbPlayers) {
        sw.start();
        channel = chan;
        numberPlayers = nbPlayers;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Lancement d'un seek = " + args.getMessage());
        }

        seekServ = args.poll();
        if ("on".equalsIgnoreCase(seekServ)) {
            ippass = args.poll();
        } else if (!"off".equalsIgnoreCase(seekServ)) {
            sw.stop();
            sw.reset();
            return "Parametre(s) Incorrect";
        }
        seekLevel = args.poll();
        seekMessage = args.poll();

        return getSeekInfo();
    }

    /**
     * @return une String
     */
    public String stopSeek() {
        seekInProgress = false;
        isLaunchedAndQuit = false;
        userpv.clear();
        split = false;
        sw.stop();
        sw.reset();
        return "Le seek est arrete.";
    }

    /**
     * @param stg
     *            Message qui vient du channel de seek
     * @return message
     */
    public boolean submitChannelMessage(String stg) {
        // On doit inverser le seek ex:Si je seek srv ON,le msg de match doit etre serv off.
        String seekServ1;
        if ("on".equalsIgnoreCase(seekServ)) {
            seekServ1 = "off";
        } else if ("off".equalsIgnoreCase(seekServ)) {
            seekServ1 = "on";
        } else {
            seekServ1 = "";
        }

        List<String> listMatch = new ArrayList<>();
        int player = numberPlayers;
        for (String element : SEPARATEUR) {
            listMatch.add(player + element + player);
            listMatch.add(player + " " + element + " " + player);
        }
        boolean numbermatch = false;
        for (String element : listMatch) {
            if (stg.toLowerCase(Locale.FRENCH).contains(element)) {
                numbermatch = true;
                break;
            }

        }
        if (numbermatch && stg.toLowerCase(Locale.FRENCH).contains(seekServ1)) {

            int i = -1;
            // On regarde si le seekLevel match avec la liste de lvl
            for (int j = 0; j < lvl.size(); j++) {
                if (lvl.get(j).equals(seekLevel.toLowerCase(Locale.FRENCH))) {
                    i = j;
                }
            }
            // Si le msg est a +-1 lvl on match
            if (i > -1) {
                if (StringUtils.containsIgnoreCase(stg, seekLevel)) {
                    return true;
                }
                if (i == 0 && StringUtils.containsIgnoreCase(stg, lvl.get(i + 1))) {
                    return true;
                } else if (i == lvl.size() - 1 && StringUtils.containsIgnoreCase(stg, lvl.get(i - 1))) {
                    return true;
                } else if (i != 0
                        && i != lvl.size() - 1
                        && (StringUtils.containsIgnoreCase(stg, lvl.get(i + 1)) || StringUtils.containsIgnoreCase(stg,
                                lvl.get(i - 1)))) {
                    return true;
                }

            } else {
                // on a pas reussi a comprendre son lvl, on lui demande en PV
                return true;

            }

        }
        return false;
    }

    /**
     * @param msg
     * @param provenance
     *            = nick
     * @return true si le bot pense que le PV est ok
     */
    private boolean submitPVMessage(String msg, IrcUser provenance) {

        Matcher m = IP_PATTERN.matcher(msg);
        if (m.find()) {
            try {
                SocketAddress add1 = NetUtils.makeISA(m.group());
                if (NetUtils.checkPortRange(add1, MIN_SRV_PORT, MAX_SRV_PORT)) {
                    // On regarde si l'user est deja dans la liste
                    if (!userpv.contains(provenance)) {
                        userpv.add(provenance);
                    }
                    return true;
                }
            } catch (IllegalArgumentException iae) {
                return false;
            }
        }

        String lowerMsg = msg.toLowerCase(Locale.FRENCH);
        for (String element : blackList) {
            if (lowerMsg.contains(element)) {
                return false;
            }
        }
        // GreenList1 LVL + GREENLIST
        Collection<String> greenList1 = new ArrayList<>(greenList.size() + lvl.size());
        greenList1.addAll(greenList);
        greenList1.addAll(lvl);

        for (String element : greenList1) {
            if (lowerMsg.contains(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param im
     * @return String
     */

    public List<Privmsg> submitSeekMessage(Privmsg im) {

        List<Privmsg> resultPrivmsg = new ArrayList<>(3);

        // On test si il faut renvoyer le msg de seek au channel

        if (sw.getTime() > 30 * 1_000) {
            IIrcServerPeer server = im.getServerPeer();
            if (split) {
                if (sw.getSplitTime() > 60 * 1_000 && !sWarning) {
                    sw.split();
                    seekMessage = "." + seekMessage + ".";
                    for (String seekChan : SEEK_CHANS) {

                        Privmsg msg1 = new Privmsg(server, null, server.getNetwork().findChannel(seekChan),
                                getMessageForSeeking());
                        resultPrivmsg.add(msg1);
                    }
                    // un deuxieme msg
                    seekMessage = "." + seekMessage + ".";
                    for (String seekChan : SEEK_CHANS) {
                        Privmsg msg1 = new Privmsg(server, null, server.getNetwork().findChannel(seekChan),
                                getMessageForSeeking());
                        resultPrivmsg.add(msg1);
                    }

                }
            } else {
                sw.split();
                split = true;
                seekMessage = ":" + seekMessage + ":";
                for (String seekChan : SEEK_CHANS) {
                    Privmsg msg1 = new Privmsg(server, null, server.getNetwork().findChannel(seekChan),
                            getMessageForSeeking());
                    resultPrivmsg.add(msg1);
                }
            }
        }

        return processIncomingMessage(im, resultPrivmsg);

    }
}
