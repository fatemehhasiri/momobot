package net.mauhiz.irc.bot.triggers.cs.query;

import java.nio.ByteBuffer;

import net.mauhiz.irc.bot.triggers.cs.IServer;
import net.mauhiz.irc.bot.triggers.cs.ServerFlags;
import net.mauhiz.util.FileUtil;

import org.apache.commons.lang3.time.StopWatch;

public class InfoQuery extends AbstractQuery implements ServerFlags {

    /**
     * char info
     */
    private static final char A2S_INFO = 'T';

    private final StopWatch sw = new StopWatch();

    public InfoQuery(IServer server) {
        super(server);
    }

    @Override
    public void afterReceive(ByteBuffer result) {
        // ignore response
        server.setPing(sw.getTime());
        sw.reset();
        char type = readByteAsChar(result);
        LOG.debug("Type: " + Character.toString(type));
        if (type == GOLDSOURCE) {
            getDetailsGoldSource(result);
        } else if (type == SOURCE) {
            getDetailsSource(result);
        } else {
            LOG.warn("Serveur inconnu (type = " + type + ")");
        }
    }

    @Override
    public void beforeSend() {
        server.setPing(-1);
        sw.start();
    }

    @Override
    public ByteBuffer getCmd() {
        return FileUtil.ASCII.encode(A2S_INFO + QUERY);
    }

    /**
     * @param result
     */
    protected void getDetailsGoldSource(ByteBuffer result) {
        String adresse = getNextString(result);
        LOG.debug("adresse: " + adresse);
        server.setName(getNextString(result));
        LOG.debug("name: " + server.getName());
        server.setMap(getNextString(result));
        LOG.debug("map: " + server.getMap());
        String gameDir = getNextString(result);
        LOG.debug("gameDir: " + gameDir);
        String gameDesc = getNextString(result);
        LOG.debug("gameDesc: " + gameDesc);
        int playerCount = result.get();
        server.setPlayerCount(playerCount);
        LOG.debug("players: " + playerCount);
        int maxPlayers = result.get();
        server.setMaxPlayers(maxPlayers);
        LOG.debug("max players: " + maxPlayers);
        short version = result.get();
        LOG.debug("version: " + version);
        char dedicated = readByteAsChar(result);
        LOG.debug("dedicated: " + dedicated);
        char operatingSystem = readByteAsChar(result);
        LOG.debug("OS: " + operatingSystem);
        boolean passWord = readByteAsBoolean(result);
        LOG.debug("passWord: " + passWord);
        boolean isMod = readByteAsBoolean(result);
        LOG.debug("isMod: " + isMod);
        if (isMod) {
            getDetailsModGoldSource(result);
        }
        boolean secure = readByteAsBoolean(result);
        LOG.debug("secure: " + secure);
        short nbBots = result.get();
        LOG.debug("nbBots: " + nbBots);

        if (result.hasRemaining()) {
            LOG.warn("remaining bytes: " + result.remaining());
            LOG.warn("remaining text: " + FileUtil.ASCII.decode(result));
        }
    }

    /**
     * @param result
     */
    protected void getDetailsModGoldSource(ByteBuffer result) {
        String urlInfo = getNextString(result);
        LOG.debug("urlInfo? " + urlInfo);

        String urlDl = getNextString(result);
        LOG.debug("urlDl? " + urlDl);

        /* byte nul */
        result.get();
        int modVersion = result.getInt();
        LOG.debug("modVersion? " + Integer.toString(modVersion));
        int modSize = result.getInt();
        LOG.debug("modSize: " + modSize);
        boolean svOnly = readByteAsBoolean(result);
        LOG.debug("svOnly? " + svOnly);
        boolean clDll = readByteAsBoolean(result);
        LOG.debug("clDll? " + clDll);

    }

    /**
     * @param result
     */
    private void getDetailsSource(ByteBuffer result) {
        short version = result.get();
        LOG.debug("protocol: " + version);
        server.setName(getNextString(result));
        LOG.debug("name: " + server.getName());
        server.setMap(getNextString(result));
        LOG.debug("map: " + server.getMap());
        String gameDir = getNextString(result);
        LOG.debug("gameDir: " + gameDir);
        String gameDesc = getNextString(result);
        LOG.debug("gameDesc: " + gameDesc);
        short appId = result.getShort();
        LOG.debug("appId: " + appId);
        int playerCount = result.get();
        server.setPlayerCount(playerCount);
        LOG.debug("players: " + playerCount);
        int maxPlayers = result.get();
        server.setMaxPlayers(maxPlayers);
        LOG.debug("max players: " + maxPlayers);
        short nbBots = result.get();
        LOG.debug("nbBots: " + nbBots);
        char dedicated = readByteAsChar(result);
        LOG.debug("dedicated: " + dedicated);
        char operatingSystem = readByteAsChar(result);
        LOG.debug("OS: " + operatingSystem);
        boolean passWord = readByteAsBoolean(result);
        LOG.debug("password? " + passWord);
        boolean secure = readByteAsBoolean(result);
        LOG.debug("secure? " + secure);
        String gameVersion = getNextString(result);
        LOG.debug("version: " + gameVersion);
    }
}
