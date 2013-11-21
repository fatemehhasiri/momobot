package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.Target;

/**
 * @author mauhiz
 */
public class List extends AbstractIrcMessage {

    public List(IIrcServerPeer server, Target from) {
        super(server, from);
    }

    @Override
    public List copy() {
        return new List(server, from);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.LIST;
    }

    @Override
    public String toString() {
        return "* Getting channel list...";
    }
}
