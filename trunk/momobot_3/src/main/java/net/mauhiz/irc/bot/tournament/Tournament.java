package net.mauhiz.irc.bot.tournament;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.mauhiz.irc.base.data.Channel;
import net.mauhiz.irc.bot.event.ChannelEvent;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * @author Topper
 */
public class Tournament extends ChannelEvent {
    /**
     * config
     */
    private static final Configuration CFG;
    /**
     * logger.
     */
    private static final Logger LOG = Logger.getLogger(Tournament.class);
    /**
     * 
     */
    private static int numberPlayerPerTeam = 5;
    static {
        try {
            CFG = new PropertiesConfiguration("tournament/tn.properties");
        } catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    /**
     * @param a
     * @param b
     * @return int
     */
    static int power(final int a, final int b) {
        if (b < 0) {
            throw new IllegalArgumentException("b must be positive");
        }
        if (b == 0) {
            return 1;
        }
        return a * power(a, b - 1);
    }
    /**
     * @param temp
     * @throws IOException
     */
    static void uploadFile(final File temp) throws IOException {
        String ftp = CFG.getString("tn.upload.to");
        URI ftpURI = URI.create(ftp);
        if ("ftp".equals(ftpURI.getScheme())) {
            FTPClient client = new FTPClient();
            int port = ftpURI.getPort();
            if (port <= 0) {
                port = FTP.DEFAULT_PORT;
            }
            client.connect(ftpURI.getHost(), port);
            client.enterLocalPassiveMode();
            String userNfo = ftpURI.getUserInfo();
            String user = StringUtils.substringBefore(userNfo, ":");
            String password = StringUtils.substringAfter(userNfo, ":");
            client.login(user, password);
            String remotePath = ftpURI.getPath();
            int slash = remotePath.lastIndexOf('/');
            String remoteDir = remotePath.substring(0, slash);
            LOG.debug("cwd to " + remoteDir);
            client.changeWorkingDirectory(remoteDir);
            
            String remoteFileName = remotePath.substring(slash);
            LOG.debug("storing to " + remoteFileName);
            InputStream is = null;
            try {
                is = new FileInputStream(temp);
                boolean success = client.storeFile(ftpURI.getPath(), is);
                if (!success) {
                    LOG.warn("Could not upload to " + ftpURI);
                }
            } finally {
                IOUtils.closeQuietly(is);
            }
            
        } else {
            throw new UnsupportedOperationException("protocol not yet supported" + ftpURI.getScheme());
        }
    }
    /**
     * 
     */
    private final List<String> mapList = new ArrayList<String>();
    /**
     * 
     */
    private List<Match> matchList = new ArrayList<Match>();
    
    /**
     * le temps o� je commence.
     */
    private final StopWatch sw = new StopWatch();
    
    /**
     * l'ensemble de joueurs. Ne sera jamais <code>null</code>
     */
    private final List<Team> teamList = new ArrayList<Team>();
    
    /**
     * @param channel1
     * @param maps
     */
    public Tournament(final Channel channel1, final String[] maps) {
        super(channel1);
        sw.start();
        int numberTeams = power(2, maps.length); // 2^4=16
        // On cr�e les teams
        if (LOG.isDebugEnabled()) {
            LOG.debug("Lancement d'un tn sur: " + channel1.toString() + " maps: " + StringUtils.join(maps));
        }
        
        for (int i = 0; i < numberTeams; i++) {
            Team team = new Team(numberPlayerPerTeam, i, "Tag", Locale.FRANCE);
            teamList.add(team);
        }
        // On Cr�e la liste de map
        for (String map : maps) {
            mapList.add(map);
        }
        int phase = mapList.size(); // 3
        for (int i = 0; i < numberTeams / 2; i++) {
            Match match = new Match(phase, i, mapList.get(0), teamList.get(2 * i), teamList.get(2 * i + 1));
            matchList.add(match);
        }
    }
    
    /**
     * @return template File.
     * @throws Exception
     */
    File createTemplateFile() throws Exception {
        VelocityContext context = new VelocityContext();
        StringBuilder maps = new StringBuilder();
        for (String map : mapList) {
            maps.append(",'");
            maps.append(map);
            maps.append('\'');
        }
        context.put("maps", maps.substring(1));
        
        VelocityEngine ve = new VelocityEngine();
        File temp = new File(CFG.getString("tn.tempfile.name"));
        
        ve.init();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(temp);
            
            Template plate = ve.getTemplate(CFG.getString("tn.vm"));
            plate.initDocument();
            plate.merge(context, writer);
            writer.flush();
            LOG.debug(FileUtils.readFileToString(temp));
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return temp;
    }
    
    /**
     * genere et upload le template
     */
    public void generateTemplate() {
        File temp = null;
        try {
            temp = createTemplateFile();
            uploadFile(temp);
        } catch (Exception ioe) {
            LOG.error(ioe, ioe);
        } finally {
            FileUtils.deleteQuietly(temp);
        }
    }
    /**
     * @return listTeam
     */
    public List<String> getListTeam() {
        List<String> reply = new ArrayList<String>();
        for (Team element : teamList) {
            reply.add(element.toString());
        }
        return reply;
    }
    /**
     * @param oldMatch
     * @param team
     * @return String
     */
    private String setNextMatch(final Match oldMatch, final Team team) {
        int newphase = oldMatch.getPhase() - 1;
        // C'�tait la finale
        if (newphase == 0) {
            // Match match = new Match(newphase, 0, mapList.get(0), team, team);
            // matchList.add(match);
            return "Bravo, team n�" + team.getId() + "=" + team.getNom() + " gagne le tournois! o//";
            
        }
        int id = team.getId();
        // int newID = id / nombreMatchPerSide;
        int newID = id / power(2, mapList.size() - newphase) / power(2, mapList.size() - newphase);
        int testMatch = testMatch(newphase, newID);
        if (testMatch == -1) {
            // le match n'existe pas
            Match match = new Match(newphase, newID, mapList.get(mapList.size() - newphase), team);
            matchList.add(match);
            // "La team " + team.getId() + " en attente du r�sultat des adversaires. next map = " + match.getMap();
            return match.toString();
            
        }
        // le match existe, on ajoute l'autre team
        Match oldmatch = matchList.remove(testMatch);
        Match match = new Match(oldmatch, team);
        matchList.add(match);
        return "Nouveau match : " + match.toString();
        
    }
    
    /**
     * @param idTeam
     *            qui a win
     * @param score1_
     *            de la team winner
     * @param score2_
     *            de la team qui a loose
     * @return List String
     */
    
    public List<String> setScore(final int idTeam, final int score1_, final int score2_) {
        List<String> reply = new ArrayList<String>();
        int score1 = score1_;
        int score2 = score2_;
        
        if (idTeam < 0 || idTeam > teamList.size() - 1) {
            reply.add("Erreur : Id team invalide");
            return reply;
        }
        
        if (score1 <= score2) {
            int score3 = score1;
            score1 = score2;
            score2 = score3;
        }
        
        for (Match match : matchList) {
            // on regarde si le match est complet ou pas
            if (match.isTeamIn(teamList.get(idTeam)) && match.getWinner() == -1) {
                reply.add(match.setScore(teamList.get(idTeam), score1, score2));
                // si le match est bien mise a jour
                if (match.isReady()) {
                    reply.add(setNextMatch(match, teamList.get(idTeam)));
                }
                return reply;
            }
        }
        reply.add("La team " + teamList.get(idTeam).getId() + " est d�ja �l�min�.");
        return reply;
    }
    
    /**
     * @param index
     * @param loc
     * @param tag
     * @param nicknames
     *            REM : $tn-register IDTEAM COUNTRY TAG PLAYER1 PLAYER2 PLAYER..
     * @return string
     */
    public String setTeam(final int index, final Locale loc, final String tag, final List<String> nicknames) {
        if (index < 0 || index > teamList.size() - 1) {
            return "Erreur index invalid";
        }
        
        Team team = teamList.get(index);
        team.setCountry(loc);
        team.setNom(tag);
        
        // On clean pour tout remettre
        team.clear();
        
        if (nicknames.size() < team.getCapacity()) {
            team.addAll(nicknames);
            for (int i = nicknames.size() + 1; i <= team.getCapacity(); i++) {
                team.add("Player " + i);
            }
        } else {
            team.addAll(nicknames.subList(0, team.getCapacity()));
        }
        return team.toString();
    }
    
    /**
     * @param phase
     * @param id
     * @return i <=> num�ro du match ou -1 si le match n'existe pas
     */
    private int testMatch(final int phase, final int id) {
        for (int i = 0; i < matchList.size(); i++) {
            Match match = matchList.get(i);
            
            if (match.getPhase() == phase && match.getID() == id) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * @see net.mauhiz.irc.bot.event.ChannelEvent#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String matchEnAttente = "";
        String matchEnCour = "";
        Match finale = null;
        for (Match element : matchList) {
            if (element.getWinner() == -1) {
                if (element.isReady()) {
                    matchEnCour += element.toString();
                } else {
                    matchEnAttente += element.toString();
                }
            }
            if (element.getPhase() == 1) {
                
                finale = element;
            }
        }
        if (finale != null) {
            return "Tounois : " + teamList.size() + " teams de " + numberPlayerPerTeam + " joueurs. finale : "
                    + finale.toString();
        }
        return "Tounois : " + teamList.size() + " teams de " + numberPlayerPerTeam + " joueurs." + " Match en cour:"
                + matchEnCour + " Match en attente :" + matchEnAttente;
    }
    
}
