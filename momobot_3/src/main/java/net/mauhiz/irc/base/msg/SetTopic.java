package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.Target;

public class SetTopic extends AbstractIrcMessage implements IrcChannelMessage {

    private final IrcChannel chan;
    private final String topic;

    public SetTopic(IIrcServerPeer server, Target from, IrcChannel chan, String newTopic) {
        super(server, from);
        topic = newTopic;
        this.chan = chan;
    }

    @Override
    public SetTopic copy() {
        return new SetTopic(server, from, chan, topic);
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
        return IrcCommands.TOPIC;
    }

    @Override
    public String getIrcForm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getIrcForm());
        sb.append(' ').append(chan);

        if (topic != null) {
            sb.append(" :").append(topic);
        }
        return sb.toString();
    }

    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return niceFromDisplay() + " changed the topic on " + chan;
    }
}
