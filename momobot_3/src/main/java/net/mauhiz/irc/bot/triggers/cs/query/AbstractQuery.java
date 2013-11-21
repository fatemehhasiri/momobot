package net.mauhiz.irc.bot.triggers.cs.query;

import java.nio.ByteBuffer;

import net.mauhiz.irc.bot.triggers.cs.IServer;

import org.apache.log4j.Logger;

public abstract class AbstractQuery implements IValveQuery {

    protected static final Logger LOG = Logger.getLogger(AbstractQuery.class);

    protected final IServer server;

    public AbstractQuery(IServer server) {
        this.server = server;
    }

    @Override
    public void beforeSend() {
        // empty by default;
    }

    /**
     * @param buffer
     *            le bytebuffer
     * @return la chaine de caracteres
     */
    protected String getNextString(ByteBuffer buffer) {
        StringBuilder retour = new StringBuilder();

        while (buffer.hasRemaining()) {
            char temp = readByteAsChar(buffer);
            if (0 == temp) {
                break;
            }
            retour.append(temp);
        }
        return retour.toString();
    }

    protected boolean readByteAsBoolean(ByteBuffer buf) {
        return 1 == buf.get();
    }

    /** and not buffer.getChar() */
    protected char readByteAsChar(ByteBuffer buf) {
        return (char) buf.get();
    }
}
