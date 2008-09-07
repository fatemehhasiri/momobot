package net.mauhiz.irc.bot.event;

import net.mauhiz.irc.DateUtils;
import net.mauhiz.irc.base.Color;
import net.mauhiz.irc.base.ColorUtils;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class Gather extends ChannelEvent {
    /**
     * La taille d'un gather.
     */
    public static final int DEFAULT_SIZE = 5;
    /**
     * s�parateur entre l'affichage des diff�rents membres du gather.
     */
    private static final String DISPLAY_SEPARATOR = " - ";
    /**
     * logger.
     */
    private static final Logger LOG = Logger.getLogger(Gather.class);
    
    /**
     * 
     */
    private SeekWar seekWar;
    // /**
    // * Un serveur?
    // */
    // private Server serv;
    /**
     * le temps o� je commence.
     */
    private final StopWatch sw = new StopWatch();
    /**
     * l'ensemble de joueurs. Ne sera jamais <code>null</code>
     */
    private final Team team;
    
    /**
     * @param channel1
     *            le channel
     */
    public Gather(final IrcChannel channel1) {
        this(channel1, "eule^", DEFAULT_SIZE);
    }
    
    /**
     * @param channel1
     * @param nbPlayers
     */
    public Gather(final IrcChannel channel1, final int nbPlayers) {
        this(channel1, "eule^", nbPlayers);
    }
    
    /**
     * @param tag
     *            le tag
     * @param channel1
     *            le channel
     * @param size
     */
    public Gather(final IrcChannel channel1, final String tag, final int size) {
        super(channel1);
        sw.start();
        team = new Team(size, tag);
    }
    
    /**
     * @param element
     *            l'element
     * @return le message d'ajout, jamais <code>null</code>.
     */
    public final String add(final IrcUser element) {
        if (element == null) {
            throw new IllegalArgumentException("element must not be null");
        }
        final StringBuilder retour = new StringBuilder();
        retour.append(element);
        
        if (team.contains(element)) {
            retour.append(": tu es d�j� inscrit.");
        } else if (team.isFull()) {
            retour.insert(0, "D�sol� ").append(", c'est complet");
        } else {
            team.add(element);
            retour.append(" ajout� au gather. ");
            switch (team.remainingPlaces()) {
                case 0 :
                    retour.append("C'est complet! Ready to rock 'n $roll");
                    break;
                case 1 :
                    retour.append("Reste une seule place!");
                    break;
                default :
                    retour.append("Reste ").append(team.remainingPlaces()).append(" places.");
                    break;
            }
        }
        return retour.toString();
    }
    
    /**
     * 
     */
    public final void createSeekWar() {
        if (seekWar == null) {
            seekWar = new SeekWar();
        }
    }
    
    /**
     * @return le nombre de joueur dans la team
     */
    public final int getNumberPlayers() {
        return team.size();
    }
    
    /**
     * @return le seekWar
     */
    public final SeekWar getSeek() {
        return seekWar;
    }
    
    // /**
    // * @return l'ip du serv
    // */
    // public final String getServ() {
    // if (null == this.serv) {
    // return "serv off :/";
    // }
    // return new StringBuilder("IP:
    // ").append(this.serv.getIp()).append(':').append(this.serv.getPort()).append(
    // " pass: ").append(this.serv.getPass()).toString();
    // }
    // /*
    // * public void setServ(String ipay, String pass){ serv =
    // NetUtils.findServ(ipay); if (null == serv) return;
    // serv.pass = pass; }
    // */
    // /**
    // * @return si on a un serv
    // */
    // public final boolean haveServ() {
    // return this.serv != null;
    // }
    /**
     * @param user
     *            un type � virer
     * @return un message
     */
    public final String remove(final IrcUser user) {
        if (user == null) {
            return "";
        }
        if (team.remove(user)) {
            return user + " a �t� retir� du gather.";
        }
        return user + ": tu n'�tais pas inscrit tfa�on.";
    }
    
    /**
     * @return un pauvre mec pris au hasard
     */
    public final String roll() {
        if (team.isEmpty()) {
            return "Personne n'est inscrit au gather.";
        }
        return "Plouf, plouf, ce sera " + team.get(RandomUtils.nextInt(team.size())) + " qui ira seek!";
    }
    
    /**
     * 
     */
    public final void setSeekToNull() {
        seekWar = null;
    }
    
    /**
     * @param string
     *            le nouveau tag
     * @return un msg
     */
    public final String setTag(final String string) {
        team.setNom(string);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Nouveau tag = " + team);
        }
        return "Nouveau tag : " + team;
    }
    
    /**
     * @return un message
     */
    @Override
    public final String toString() {
        final StringBuilder temp = new StringBuilder(ColorUtils.toColor("Gather " + team.size() + '/'
                + team.getCapacity(), Color.BROWN));
        temp.append(" (start: ");
        temp.append(ColorUtils.toColor(DateUtils.getTimeStamp(sw), Color.DARK_GREEN));
        temp.append(") (tag: ");
        temp.append(ColorUtils.toColor(team.toString(), Color.RED));
        temp.append(") ");
        if (team.isEmpty()) {
            return temp.toString();
        }
        for (final IrcUser ircUser : team) {
            temp.append(ircUser).append(DISPLAY_SEPARATOR);
        }
        return temp.substring(0, temp.length() - DISPLAY_SEPARATOR.length());
    }
}
