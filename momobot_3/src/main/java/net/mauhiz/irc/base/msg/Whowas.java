package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.Target;

/**
 * @author mauhiz
 */
public class Whowas extends AbstractIrcMessage {

    private final String target;

    public Whowas(IIrcServerPeer server, Target from, String target) {
        super(server, from);
        this.target = target;
    }

    @Override
    public Whowas copy() {
        return new Whowas(server, from, target);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.WHOWAS;
    }

    @Override
    public String getIrcForm() {
        return super.getIrcForm() + " " + target;
    }

    @Override
    public String toString() {
        return "* Who was " + target;
    }
}
