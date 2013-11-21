package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.IrcUser;

/**
 * @author mauhiz
 */
public class Nick extends AbstractIrcMessage {
    /**
     * le nouveau nick
     */
    private final String newNick;

    public Nick(IIrcServerPeer server) {
        this(server, null, server.getMyself().getNick());
    }

    public Nick(IIrcServerPeer server, IrcUser from, String newNick) {
        super(server, from);
        this.newNick = newNick;
    }

    @Override
    public Nick copy() {
        return new Nick(server, getFrom(), newNick);
    }

    @Override
    public IrcUser getFrom() {
        return (IrcUser) super.getFrom();
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.NICK;
    }

    @Override
    public String getIrcForm() {
        return super.getIrcForm() + " " + newNick;
    }

    /**
     * @return {@link #newNick}
     */
    public String getNewNick() {
        return newNick;
    }

    @Override
    public String toString() {
        if (from == null) {
            return "* Setting new nickname " + newNick;
        }
        return "* " + niceFromDisplay() + " is now known as " + newNick;
    }
}
