package net.mauhiz.irc.bot.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;

/**
 * @author mauhiz
 */
public class Pickup extends ChannelEvent {
    /**
     * le nombre de teams.
     */
    private static final int NB_TEAMS = 2;
    /**
     * la taille des teams.
     */
    private static final int SIZE = 5;
    /**
     * les teams.
     */
    private final List<Team> teams = new ArrayList<Team>(NB_TEAMS);
    
    // private Server serv = null;
    /**
     * @param channel1
     *            le channel
     */
    public Pickup(final IrcChannel channel1) {
        super(channel1);
        for (char nom = 'a'; teams.size() < NB_TEAMS; ++nom) {
            teams.add(new Team(SIZE, Character.toString(nom)));
        }
    }
    
    /**
     * @param a
     *            l'�l�ment � ajouter
     * @param choix
     *            le choix
     * @return un msg
     */
    public final String add(final IrcUser a, final String choix) {
        if (isFull()) {
            return "C'est complet!";
        }
        synchronized (teams) {
            final Team assignedTeam = assignTeam(choix);
            final Team currentTeam = getTeam(a);
            if (null == currentTeam) {
                /* element n'est pas encore pr�sent */
                if (null == assignedTeam) {
                    /* element n'a pas choisi de team en particulier */
                    for (final Team tryTeam : teams) {
                        /*
                         * on essaye de le mettre dans la premi�re �quipe qu'on trouve
                         */
                        if (tryTeam.add(a)) {
                            return a + " ajout� a la team " + tryTeam + '.';
                        }
                    }
                    /* on ne doit pas arriver l� */
                    throw new IllegalStateException("J'ai pas r�ussi � ajouter " + a + " au gather.");
                }
                /* pas de probl�me, il a choisi, on l'ajoute */
                assignedTeam.add(a);
                return a + " ajout� a la team " + assignedTeam + '.';
            }
            /* element est d�j� pr�sent et ne veut pas changer d'�quipe */
            if (null == assignedTeam || assignedTeam.equals(currentTeam)) {
                return "Tu es d�j� inscrit dans la team " + currentTeam + '.';
            }
            /* element est d�j� pr�sent et veut changer d'�quipe */
            currentTeam.remove(a);
            assignedTeam.add(a);
            return a + " d�plac� vers la team " + assignedTeam + '.';
        }
    }
    
    /**
     * @param choix
     *            le choix
     * @return le numero de la team dans laquelle il est autoassign
     */
    public final Team assignTeam(final String choix) {
        if (choix == null) {
            return null;
        }
        synchronized (teams) {
            for (final Team team : teams) {
                /* le equals est null-safe dans ce sens */
                if (team.toString().equalsIgnoreCase(choix) && !team.isFull()) {
                    return team;
                }
            }
            return null;
        }
    }
    
    /**
     * @param a
     *            l'utilisateur � inscrire
     * @return le numero de la team dans laquelle il est inscrit
     */
    public final Team getTeam(final IrcUser a) {
        for (final Team team : teams) {
            if (team.contains(a)) {
                return team;
            }
        }
        return null;
    }
    
    /**
     * @return si le pickup est full
     */
    public final boolean isFull() {
        for (final Team team : teams) {
            if (!team.isFull()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param element
     *            �l�ment � retirer
     * @return un msg
     */
    public final String remove(final IrcUser element) {
        if (element == null) {
            return "";
        }
        final Team findTeam = getTeam(element);
        if (null == findTeam) {
            return element + ": tu n'�tais pas inscrit.";
        }
        findTeam.remove(element);
        return element + " retir� de la team " + findTeam + '.';
    }
    
    /**
     * @return un $status
     */
    public String shake() {
        List<IrcUser> listeUser = new ArrayList<IrcUser>(NB_TEAMS * SIZE);
        
        synchronized (teams) {
            
            // On Ajoute les user dans la liste
            for (final Team team : teams) {
                listeUser.addAll(team);
                team.clear();
            }
            
            Collections.shuffle(listeUser);
            
            // On re-remplit les teams
            int indexTeam = 0;
            for (IrcUser next : listeUser) {
                teams.get(indexTeam).add(next);
                indexTeam = ++indexTeam % NB_TEAMS;
            }
        }
        return "Shake R�ussi!";
    }
    
    /**
     * @see net.mauhiz.irc.bot.event.ChannelEvent#toString()
     */
    @Override
    public final String toString() {
        final StringBuilder retour = new StringBuilder();
        for (final Team team : teams) {
            retour.append("Team " + team + ": ");
            for (final IrcUser user : team) {
                retour.append(user).append(". ");
            }
        }
        return retour.toString();
    }
    // public String getServ() {
    // return "Serv: " + serv.getIp() + ":" + serv.getPort() + " - Pass: " +
    // serv.pass;
    // }
    //
    // public void setServ(String ipay, String pass, String rcon) {
    // serv = NetUtils.findServ(ipay);
    // if (null == serv)
    // return;
    // serv.pass = pass;
    // serv.setRcon(rcon);
    // }
    //
    // public void setServ(String ipay, String pass) {
    // setServ(ipay, pass, null);
    // }
}
