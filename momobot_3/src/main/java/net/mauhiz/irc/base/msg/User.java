package net.mauhiz.irc.base.msg;

import java.util.Locale;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;

/**
 * @author mauhiz
 */
public class User extends AbstractIrcMessage {
    private final String fullName;
    private final String login;

    /**
     * @param server
     */
    public User(IIrcServerPeer server) {
        super(server, null);
        login = server.getMyself().getMask().getUser();
        fullName = server.getMyself().getFullName();
    }

    @Override
    public User copy() {
        return new User(server);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.USER;
    }

    @Override
    public String getIrcForm() {
        return super.getIrcForm() + " " + login.toLowerCase(Locale.US) + " \"neuf.fr\" \"irc.quakenet.org\" :"
                + fullName;
    }
}
