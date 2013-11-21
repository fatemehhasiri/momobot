package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.Target;

/**
 * @author mauhiz
 */
public class Privmsg extends AbstractPrivateIrcMessage {

    public Privmsg(IIrcServerPeer server, Target from, Target to, String message) {
        super(server, from, to, message);
    }

    public Privmsg(IPrivateIrcMessage replyTo, String respMsg) {
        this(replyTo, respMsg, false);
    }

    /**
     * Builds an answer
     */
    public Privmsg(IPrivateIrcMessage replyTo, String respMsg, boolean priv) {
        super(replyTo, respMsg, priv);
    }

    @Override
    public Privmsg copy() {
        return new Privmsg(server, from, to, message);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.PRIVMSG;
    }

    @Override
    public String toString() {
        return "<" + niceFromDisplay() + "> " + message;
    }
}
