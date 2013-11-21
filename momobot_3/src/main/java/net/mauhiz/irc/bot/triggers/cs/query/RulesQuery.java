package net.mauhiz.irc.bot.triggers.cs.query;

import java.nio.ByteBuffer;

import net.mauhiz.irc.bot.triggers.cs.IServer;
import net.mauhiz.irc.bot.triggers.cs.ServerFlags;
import net.mauhiz.util.FileUtil;

public class RulesQuery extends AbstractQuery implements ServerFlags {
    /**
     * char info
     */
    private static final char A2S_RULES = 'V';

    public RulesQuery(IServer server) {
        super(server);
    }

    @Override
    public void afterReceive(ByteBuffer result) {
        char state = readByteAsChar(result); // skip char 'D'
        if (state == S2C_CHALLENGE) { // response is not rules, but challenge
            ChallengeQuery.processChallenge(server, result);
            return;
        }
        int nbRules = result.getShort(); // skip short number of rules
        for (int i = 0; i < nbRules; i++) {
            if (result.remaining() == 0) {
                break;
            }
            String name = getNextString(result);
            String value = getNextString(result);
            LOG.debug(name + "=" + value);
        }
    }

    @Override
    public ByteBuffer getCmd() {
        ByteBuffer cmd = FileUtil.ASCII.encode(Character.toString(A2S_RULES));
        ByteBuffer buf = ByteBuffer.allocate(cmd.limit() + 4);
        buf.put(cmd);
        buf.putInt(server.getChallenge());
        return buf;

    }
}
