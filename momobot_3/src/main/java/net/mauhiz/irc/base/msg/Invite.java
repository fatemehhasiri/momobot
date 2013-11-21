package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.data.Target;

/**
 * @author mauhiz
 */
public class Invite extends AbstractIrcMessage implements IrcChannelMessage {

    private final IrcChannel chan;
    private final IrcUser invitedUser;

    public Invite(IIrcServerPeer server, Target from, IrcUser invitedUser, IrcChannel chan) {
        super(server, from);
        this.chan = chan;
        this.invitedUser = invitedUser;
    }

    @Override
    public Invite copy() {
        return new Invite(server, from, invitedUser, chan);
    }

    /**
     * @return the message
     */
    public IrcChannel getChan() {
        return chan;
    }

    @Override
    public IrcChannel[] getChans() {
        return new IrcChannel[] { chan };
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.INVITE;
    }

    @Override
    public String getIrcForm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getIrcForm());
        sb.append(' ').append(invitedUser);
        sb.append(' ').append(chan);
        return sb.toString();
    }

    @Override
    public String toString() {
        return " * " + niceFromDisplay() + " invited me to " + chan;
    }
}
