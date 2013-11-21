package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;

/**
 * @author mauhiz
 */
public class Pong extends AbstractIrcMessage {
    private final String pingId;

    public Pong(IIrcServerPeer server, String pingId) {
        super(server, null);
        this.pingId = pingId;
    }

    @Override
    public Pong copy() {
        return new Pong(server, pingId);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.PONG;
    }

    @Override
    public String getIrcForm() {
        return super.getIrcForm() + ' ' + pingId;
    }

    @Override
    public String toString() {
        return "Answering PING: " + pingId;
    }
}
