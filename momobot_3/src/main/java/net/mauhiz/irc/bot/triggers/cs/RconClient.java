package net.mauhiz.irc.bot.triggers.cs;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Objects;

import net.mauhiz.util.FileUtil;
import net.mauhiz.util.UtfChar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;

/**
 * @author mauhiz
 */
class RconClient extends ValveUdpClient implements IRconClient {

    /**
     * challenger!
     */
    private static final String CHALLENGE = "challenge rcon";

    /**
     * 
     */
    private static final String CMD_MAP = "!map ";

    /**
     * Mon rcon.
     */
    private String rcon;
    /**
     * mon challenge.
     */
    private String rconChallenge;

    /**
     * ah, mon ecouteur prefere.
     */
    private final IRconListener rl;

    /**
     * @param server
     *            le serveur
     */
    public RconClient(IRconServer server, String rcon) throws IOException {
        super(server);
        changeRcon(rcon);
        rl = new RconListener(this);
    }

    /**
     * @param rcon1
     *            le rcon
     */
    @Override
    public void changeRcon(String rcon1) {
        if (StringUtils.isEmpty(rcon1)) {
            return;
        }
        rcon = rcon1;
        if (!rl.isRunning()) {
            initLogThread();
        }
    }

    /**
     * @throws IOException
     *             en cas de pepin!
     */
    @Override
    public void getRconChallenge() throws IOException {
        CharBuffer challengeResp = FileUtil.ASCII.decode(sendAndRcvValveCmd(CHALLENGE));
        rconChallenge = StringUtils.substringAfter(challengeResp.toString(), CHALLENGE + " ");
    }

    IRconServer getServer() {
        return (IRconServer) server;
    }

    /**
     */
    public void initLogThread() {
        Objects.requireNonNull(rcon, "No defined rcon password");
        rl.setName("Valve Udp Listener on " + server.getIp());
        rl.tstart();
    }

    private void obeyMyMaster(String action) throws IOException {
        IRconServer rcs = getServer();
        if ("!rs".equals(action)) {
            rcs.svRestart(1);
        } else if ("!3rs".equals(action)) {
            rcs.svRestart(1);
            rcs.svRestart(1);
            rcs.svRestart(3);
        } else {
            if (action.startsWith(CMD_MAP)) {
                rcs.changelevel(action.substring(CMD_MAP.length()));
            }
        }
    }

    /**
     * @param line
     *            la ligne a etudier
     */
    @Override
    public void processLine(String line) {
        if (line.startsWith("log ")) {
            try {
                processLog(StringUtils.substringAfter(line, ": "));
            } catch (IOException ioe) {
                LOG.warn(ioe, ioe);
            }
        } else {
            // String retCode = line.substring(0, 1);
            String realLine = line.substring(1);
            if ("Bad rcon_password.".equals(realLine)) {
                LOG.warn("mauvais rcon");
                unsetRcon();
            } else if ("Bad challenge.".equals(realLine)) {
                LOG.warn("mauvais challenge");
                rconChallenge = null;
            } else {
                /* reponse au status */
                if (realLine.startsWith("hostname")) {
                    processStatus(realLine);
                    return;
                }
                /* cvar quelconque. Forme : "sv_restart" "1" */
                if (UtfChar.charAt(realLine, 0).isEquals('"')) {
                    String[] pair = StringUtils.split(realLine, '"');
                    LOG.debug("CVAR : " + pair[0] + " = " + pair[2]);
                }
            }
        }
    }

    /**
     * @param log
     *            le log a etudier
     */
    private void processLog(String log) throws IOException {
        LOG.debug(log);
        if (!UtfChar.charAt(log, 0).isEquals('"')) {
            return;
        }
        String temp = StringUtils.stripStart(log, "\"");
        String playerName = StringUtils.substringBefore(temp, "<");
        temp = StringUtils.substringAfter(temp, "<");
        String steamid = StringUtils.substringAfter(temp, "<");
        steamid = steamid.substring(0, steamid.indexOf('>'));
        String action = temp.substring(temp.indexOf('"') + 2);
        if (action.startsWith("say_team") && PlayerDB.isMaster(steamid)) {
            String realAction = action.substring(10, action.lastIndexOf('"'));
            LOG.info(playerName + " (" + steamid + ") orders me :" + realAction);
            obeyMyMaster(realAction);

        }
    }

    /**
     * @param status
     *            le message de status
     */
    private void processStatus(String status) {
        LOG.debug("Status: " + status);
        if (status.length() < 25) {
            throw new IllegalArgumentException("status trop court: " + status);
        }
        StrTokenizer lignes = new StrTokenizer(status);
        // 1 - hostname
        // 1hostname: Counter-Strike dedicated server
        server.setName(StringUtils.substringAfter(lignes.nextToken(), ":").trim());
        // 2 - version (osef)
        // version : 47/1.1.2.5/2.0.0.0 2834 insecure
        lignes.nextToken();
        // 3 - adresse (on l'a deja...)
        // tcp/ip : 192.168.1.2:27015
        lignes.nextToken();
        // 4 - map
        // map : de_airstrip at: 0 x, 0 y, 0 z
        String map = StringUtils.substringAfter(lignes.nextToken(), ":").trim();
        server.setMap(StringUtils.substringBefore(map, " "));
        // 5 - player count
        // players : 0 active (24 max)
        String players = StringUtils.substringAfter(lignes.nextToken(), ":").trim();
        server.setPlayerCount(Byte.parseByte(StringUtils.substringBefore(players, " ")));
        players = StringUtils.substringAfter(players, "(");
        server.setMaxPlayers(Byte.parseByte(StringUtils.substringBefore(players, " ")));
        // 7 - ligne inutile
        // # name userid uniqueid frag time ping loss adr
        lignes.nextToken();

        server.resetPlayers();
        while (lignes.hasNext()) {
            String line = lignes.nextToken();
            if (!UtfChar.charAt(line, 0).isEquals('#')) {
                break;
            }
            traiteLigneJoueur(line.substring(2));
        }
    }

    /**
     * @param cmd
     *            la commande rcon
     */
    @Override
    public void rconCmd(String cmd) throws IOException {
        /* antiban */
        Objects.requireNonNull(rcon, "No defined rcon password");
        Objects.requireNonNull(rconChallenge, "Did not receive rcon challenge");

        sendValveCmd("rcon " + rconChallenge + " \"" + rcon + "\" " + cmd);
    }

    /**
     * @param line
     */
    private void traiteLigneJoueur(String line) {
        StrTokenizer st3 = new StrTokenizer(line);
        /* numero de joueur */
        int index = Integer.parseInt(st3.nextToken());
        /* nom du joueur : virer les guillemets */
        String playernick = StringUtils.strip(st3.nextToken(), "\"");
        /* numero de joueur (bis) */
        st3.nextToken();
        /* steam_id */
        String steamid = st3.nextToken();
        int frags = Integer.parseInt(st3.nextToken());
        /* le reste osef pour l'instant */
        Player player = new Player(steamid);
        player.setName(playernick);
        player.setFrags(frags);
        server.setPlayer(index, player);
    }

    /**
     * efface le rcon.
     */
    private void unsetRcon() {
        rcon = null;
        rl.tstop();
    }
}
