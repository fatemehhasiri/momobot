package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.Target;

/**
 * @author mauhiz
 */
public class Ping extends AbstractIrcMessage {
    private final String pingId;

    public Ping(IIrcServerPeer server, Target from, String pingId) {
        super(server, from);
        this.pingId = pingId;
    }

    @Override
    public Ping copy() {
        return new Ping(server, from, pingId);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.PING;
    }

    @Override
    public String getIrcForm() {
        return super.getIrcForm() + ' ' + pingId;
    }

    /**
     * @return the pingId
     */
    public String getPingId() {
        return pingId;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Answering PING: " + pingId;
    }
}
