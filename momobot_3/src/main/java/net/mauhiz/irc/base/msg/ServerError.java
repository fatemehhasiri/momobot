package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;

/**
 * @author mauhiz
 */
// ERROR :Closing Link: by underworld2.no.quakenet.org (Registration Timeout)
public class ServerError extends AbstractIrcMessage {
    /**
     * contenu de l erreur
     */
    private final String msg;

    public ServerError(IIrcServerPeer server, String msg) {
        super(server, null);
        this.msg = msg;
    }

    @Override
    public ServerError copy() {
        return new ServerError(server, msg);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.ERROR;
    }

    @Override
    public String getIrcForm() {
        return super.getIrcForm() + " :" + msg;
    }

    /**
     * @return {@link #msg}
     */
    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return getIrcForm();
    }
}
