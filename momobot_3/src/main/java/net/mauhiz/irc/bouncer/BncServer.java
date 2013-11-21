package net.mauhiz.irc.bouncer;

import java.util.Collection;
import java.util.Collections;

import net.mauhiz.irc.base.data.AbstractIrcNetwork;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.NumericReplies;
import net.mauhiz.irc.base.msg.ServerMsg;

public class BncServer extends AbstractIrcNetwork {

    protected BncServer(String alias) {
        super(alias);
    }

    @Override
    public int getLineMaxLength() {
        return 512;
    }

    @Override
    public Collection<String> getServiceNicks() {
        return Collections.emptySet();
    }

    @Override
    public void handleSpecific(ServerMsg message, NumericReplies reply) {
        LOG.warn("Unhandled command: " + message);
    }

    @Override
    public IrcChannel newChannel(String chanLowerCase) {
        return null;
    }

    @Override
    public BncUser newUser(String nick) {
        return null;
    }
}
