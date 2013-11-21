package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.Target;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mauhiz
 */
public class Whois extends AbstractIrcMessage {

    private final String[] targets;

    public Whois(IIrcServerPeer server, Target from, String... targets) {
        super(server, from);
        this.targets = targets;
    }

    @Override
    public Whois copy() {
        return new Whois(server, from, targets);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.WHOIS;
    }

    @Override
    public String getIrcForm() {
        return super.getIrcForm() + " " + StringUtils.join(targets, ',');
    }

    @Override
    public String toString() {
        return "* Whoising " + StringUtils.join(targets, ' ');
    }
}
