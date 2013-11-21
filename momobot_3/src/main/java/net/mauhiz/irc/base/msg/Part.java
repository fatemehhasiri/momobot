package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.IrcUser;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mauhiz
 */
public class Part extends AbstractIrcMessage implements IrcChannelMessage {
    private final IrcChannel[] fromChannels;
    private final String reason;

    public Part(IIrcServerPeer server, IrcChannel... fromChannels) {
        this(server, null, fromChannels);
    }

    public Part(IIrcServerPeer server, IrcUser from, String reason, IrcChannel... fromChannels) {
        super(server, from);
        this.reason = reason;
        this.fromChannels = fromChannels;
    }

    public Part(IIrcServerPeer server, String reason, IrcChannel... fromChannels) {
        this(server, null, reason, fromChannels);
    }

    @Override
    public Part copy() {
        return new Part(server, getFrom(), reason, fromChannels);
    }

    public IrcChannel[] getChannels() {
        return fromChannels;
    }

    @Override
    public IrcChannel[] getChans() {
        return fromChannels;
    }

    @Override
    public IrcUser getFrom() {
        return (IrcUser) super.getFrom();
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.PART;
    }

    @Override
    public String getIrcForm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getIrcForm());
        sb.append(' ').append(StringUtils.join(fromChannels, ','));
        if (reason != null) {
            sb.append(" :").append(reason);
        }
        return sb.toString();
    }

    /**
     * @return {@link #reason}
     */
    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        if (from == null) {
            return "* Leaving " + StringUtils.join(fromChannels, ' ') + (reason == null ? "" : " (" + reason + ")");
        }
        return "* " + niceFromDisplay() + " has left " + StringUtils.join(fromChannels, ' ')
                + (reason == null ? "" : " (" + reason + ")");
    }
}
