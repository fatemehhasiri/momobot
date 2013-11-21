package net.mauhiz.irc.bot.triggers.cs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * une sorte de gros bean. Les joueurs presents: Cle = steamId, Valeur = objet Player associe.
 * 
 * @author mauhiz
 */
public class Server implements IServer {
    /**
     * logger.
     */
    protected static final Logger LOG = Logger.getLogger(Server.class);

    private int challenge = -1;
    /**
     * Ip et port du serveur.
     */
    protected SocketAddress ipay;
    /**
     * map actuelle.
     */
    private String map;
    /**
     * capacite du serveur en nombre de joueurs.
     */
    private int maxPlayers;
    /**
     * hostname du serveur.
     */
    private String name;
    /**
     * mot de passe.
     */
    private String pass;
    /**
     * Temps de latence en ms.
     */
    private long ping = -1;
    /**
     * Nombre de joueurs presents sur le serveur. Utilise avant d'avoir tous les joueurs.
     */
    private int playerCount;
    /**
     * 
     */
    private final List<Player> players = new ArrayList<>();
    /**
     * Le client Valve UDP.
     */
    protected IClient vuc;

    /**
     * Constructeur.
     * 
     * @param ipay1
     *            l'IP et port du serveur.
     */
    public Server(SocketAddress ipay1) {
        super();
        ipay = ipay1;
    }

    /**
     * @return the challenge
     */
    @Override
    public int getChallenge() {
        return challenge;
    }

    public IClient getClient() throws IOException {
        if (vuc == null) {
            vuc = new ValveUdpClient(this);
        }
        return vuc;
    }

    /**
     * affiche les details du serveur. Voir <a href="http://developer.valvesoftware.com/wiki/Server_Queries#A2S_INFO">
     * VDW </a>
     */
    @Override
    public void getDetails() throws IOException {
        try (IClient client = getClient()) {
            client.getInfo();
            client.getPlayers();
            client.getPlayers(); // 2eme fois pour utiliser le Challenge
            client.getRules();
        }
    }

    /**
     * @return ip
     */
    @Override
    public String getIp() {
        return getIpay().getAddress().getHostAddress();
    }

    /**
     * Assumes the ipay is a network ipay
     * @return ipay
     */
    @Override
    public InetSocketAddress getIpay() {
        return (InetSocketAddress) ipay;
    }

    /**
     * @return Returns the map.
     */
    @Override
    public String getMap() {
        return map;
    }

    /**
     * @return maxPlayers
     */
    public String getMaxPlayers() {
        return Integer.toString(maxPlayers);
    }

    /**
     * @return Returns the name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return Returns the pass.
     */
    public String getPass() {
        return pass;
    }

    /**
     * @return Returns the ping.
     */
    public long getPing() {
        return ping;
    }

    /**
     * @return playerCount
     */
    public String getPlayerCount() {
        return Integer.toString(playerCount);
    }

    /**
     * @param steamid
     *            le SteamId
     * @return le nick du player
     */
    public String getPlayerNick(String steamid) {
        for (Player player : players) {
            if (steamid.equals(player.getSteamId())) {
                return player.getName();
            }
        }
        return null;
    }

    /**
     * @return {@link #players}
     */
    @Override
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @return port
     */
    @Override
    public int getPort() {
        return getIpay().getPort();
    }

    @Override
    public void resetPlayers() {
        players.clear();
    }

    /**
     * @param int1
     *            challenge
     */
    @Override
    public void setChallenge(int int1) {
        challenge = int1;
    }

    /**
     * @param ipay1
     *            the ipay to set
     */
    protected void setIpay(InetSocketAddress ipay1) {
        ipay = ipay1;
    }

    /**
     * @param map1
     *            The map to set.
     */
    @Override
    public void setMap(String map1) {
        map = map1;
    }

    /**
     * @param maxPlayers1
     *            le nombre maxi de players
     */
    @Override
    public void setMaxPlayers(int maxPlayers1) {
        maxPlayers = maxPlayers1;
    }

    /**
     * @param name1
     *            The name to set.
     */
    @Override
    public void setName(String name1) {
        name = name1;
    }

    /**
     * @param pass1
     *            The pass to set.
     */
    public void setPass(String pass1) {
        pass = pass1;
    }

    /**
     * @param ping1
     *            The ping to set.
     */
    @Override
    public void setPing(long ping1) {
        ping = ping1;
    }

    /**
     * @param index
     * @param player
     */
    @Override
    public void setPlayer(int index, Player player) {
        if (index > players.size()) {
            for (int i = players.size(); i < index; i++) {
                players.add(null);
            }
        }
        if (index == players.size()) {
            players.add(player);
        } else {
            players.set(index, player);
        }

    }

    /**
     * @param playerCount1
     *            le nombre de joueurs courants
     */
    @Override
    public void setPlayerCount(int playerCount1) {
        playerCount = playerCount1;
    }

    /**
     * @return un affichage
     * @see Object#toString()
     */
    @Override
    public String toString() {
        try {
            getDetails();
            return name + " | " + map + " | " + ping + "ms (" + playerCount + "/" + maxPlayers + ")";
        } catch (IOException ioe) {
            LOG.warn(ioe, ioe);
            return "Connection to server failed";
        }
    }
}
