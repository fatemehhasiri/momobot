package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.IrcUser;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mauhiz
 */
public class Join extends AbstractIrcMessage implements IrcChannelMessage {
    private final IrcChannel[] chans;
    private final String[] keys;

    public Join(IIrcServerPeer server, IrcChannel... chans) {
        this(server, null, chans);
    }

    public Join(IIrcServerPeer server, IrcUser from, IrcChannel... chans) {
        this(server, from, chans, null);
    }

    /**
     * TODO join multiple chans with different keys
     */
    public Join(IIrcServerPeer server, IrcUser from, IrcChannel[] chans, String[] keys) {
        super(server, from);
        this.keys = keys;
        this.chans = chans;
    }

    @Override
    public Join copy() {
        return new Join(server, getFrom(), chans, keys);
    }

    @Override
    public IrcChannel[] getChans() {
        return chans;
    }

    @Override
    public IrcUser getFrom() {
        return (IrcUser) super.getFrom();
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.JOIN;
    }

    @Override
    public String getIrcForm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getIrcForm());
        sb.append(' ').append(StringUtils.join(chans, ','));

        if (keys != null) {
            sb.append(' ').append(StringUtils.join(keys, ','));
        }
        return sb.toString();
    }

    /**
     * @return the message
     */
    public String[] getKeys() {
        return keys;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (from == null) { // self
            return "Joining " + StringUtils.join(chans, ' ');
        }
        return "* Joins " + chans[0] + " : " + niceFromDisplay();
    }
}
