package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.data.Target;

/**
 * @author mauhiz
 */
public class Kick extends AbstractIrcMessage implements IrcChannelMessage {
    private final IrcChannel chan;
    private final String reason;
    private final IrcUser target;

    public Kick(IIrcServerPeer server, Target kicker, IrcChannel chan, IrcUser target, String reason) {
        super(server, kicker);
        this.target = target;
        this.reason = reason;
        this.chan = chan;
    }

    @Override
    public Kick copy() {
        return new Kick(server, from, chan, target, reason);
    }

    public IrcChannel getChan() {
        return chan;
    }

    @Override
    public IrcChannel[] getChans() {
        return new IrcChannel[] { chan };
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.KICK;
    }

    @Override
    public String getIrcForm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getIrcForm());
        sb.append(' ').append(chan);
        sb.append(' ').append(target);
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

    /**
     * @return {@link #target}
     */
    public IrcUser getTarget() {
        return target;
    }

    @Override
    public String toString() {
        if (server.getMyself().equals(target)) {
            if (from == null || server.getMyself().equals(from)) {
                return "* Kicking myself from " + chan + (reason == null ? "" : ": " + reason);
            }
            // I have been kicked
            return "* Kicked from " + chan + " by " + niceFromDisplay() + (reason == null ? "" : ": " + reason);
        } else if (from == null) { // I am kicking
            return "* Kicking " + target + " from " + chan + (reason == null ? "" : ": " + reason);
        } else { // Someone kicks someone else
            return "* " + target + " was kicked from " + chan + " by " + niceFromDisplay()
                    + (reason == null ? "" : ": " + reason);
        }
    }
}
