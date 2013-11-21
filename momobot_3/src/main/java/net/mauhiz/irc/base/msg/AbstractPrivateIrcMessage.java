package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.Target;

/**
 * Generic IRC Message
 * 
 * @author mauhiz
 */
public abstract class AbstractPrivateIrcMessage extends AbstractIrcMessage implements IPrivateIrcMessage,
        IrcChannelMessage {
    protected final String message;
    protected final Target to;

    public AbstractPrivateIrcMessage(IIrcServerPeer server, Target from, Target to, String msg) {
        super(server, from);
        this.to = to;
        message = msg;
    }

    public AbstractPrivateIrcMessage(IPrivateIrcMessage toReply, String msg, boolean priv) {
        this(toReply.getServerPeer(), null, !priv && toReply.isToChannel() ? toReply.getTo() : toReply.getFrom(), msg);
    }

    @Override
    public IrcChannel[] getChans() {
        return isToChannel() ? new IrcChannel[] { (IrcChannel) to } : new IrcChannel[0];
    }

    protected int getHeaderLength() {
        return getIrcCommand().toString().length() + 1 + to.getIrcForm().length() + 2;
    }

    @Override
    public final String getIrcForm() {
        return super.getIrcForm() + ' ' + to + " :" + getMessage(); // getMessage() important pour les sous classes (CTCP, ACTION...);
    }

    public int getMaxPayload() {
        return getServerPeer().getNetwork().getLineMaxLength() - getHeaderLength();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Target getTo() {
        return to;
    }

    @Override
    public boolean isToChannel() {
        return to instanceof IrcChannel;
    }
}
