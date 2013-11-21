package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.Target;

/**
 * @author mauhiz
 */
public class Notice extends AbstractPrivateIrcMessage {

    /**
     * @deprecated
     */
    @Deprecated
    public Notice(IIrcServerPeer server, Target from, IIrcServerPeer to, String msg) {
        super(server, from, to, msg);
    }

    public Notice(IIrcServerPeer server, Target from, Target to, String msg) {
        super(server, from, to, msg);
    }

    public Notice(IPrivateIrcMessage toReply, String msg, boolean priv) {
        super(toReply, msg, priv);
    }

    @Override
    public Notice copy() {
        return new Notice(server, from, to, message);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.NOTICE;
    }

    /**
     * @return the message
     */
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (from == null) {
            return "Noticing: " + message;
        }
        return "-" + niceFromDisplay() + "- " + message;
    }
}
