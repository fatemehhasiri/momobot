package net.mauhiz.irc.bot.triggers.cs;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * une sorte de gros bean. Les joueurs presents: Cle = steamId, Valeur = objet Player associe.
 * 
 * @author mauhiz
 */
public class RconServer extends Server implements IRconServer {

    /**
     * Constructeur.
     * 
     * @param ipay1
     *            l'IP et port du serveur.
     */
    public RconServer(SocketAddress ipay1, String rcon) throws IOException {
        super(ipay1);
        vuc = new RconClient(this, rcon);
    }

    /**
     * @param cvar
     *            la cvar a demander
     */
    public void askCvar(String cvar) throws IOException {
        getClient().rconCmd(cvar);
    }

    /**
     * @param newmap
     *            la nouvelle map
     * @return si on a effectivement change de map
     */
    @Override
    public boolean changelevel(String newmap) throws IOException {
        if (newmap.equals(getMap())) {
            return false;
        }
        getClient().rconCmd("changelevel " + newmap);
        return true;
    }

    @Override
    public void close() throws IOException {
        getClient().close();
    }

    @Override
    public IRconClient getClient() throws IOException {
        return (IRconClient) super.getClient();
    }

    /**
     * @param steamid
     *            le SteamId
     * @return le nick du player
     */
    @Override
    public String getPlayerNick(String steamid) {
        for (Player player : getPlayers()) {
            if (steamid.equals(player.getSteamId())) {
                return player.getName();
            }
        }
        return null;
    }

    /**
     * Affiche le status.
     */
    public void rconStatus() throws IOException {
        getClient().rconCmd("status");
    }

    /**
     * @param cvar
     *            la cvar
     * @param value
     *            la valeur
     */
    public void setCvar(String cvar, String value) throws IOException {
        getClient().rconCmd('"' + cvar + "\" \"" + value + '"');
    }

    /**
     * recupere le password.
     */
    public void svPassword() throws IOException {
        askCvar("sv_password");
    }

    /**
     * Fixe un password.
     * 
     * @param pwd
     *            le password
     */
    public void svPassword(String pwd) throws IOException {
        setCvar("sv_password", pwd);
    }

    /**
     * @param delay
     *            le delai avant le restart
     */
    @Override
    public void svRestart(int delay) throws IOException {
        setCvar("sv_restart", Integer.toString(delay));
    }
}
