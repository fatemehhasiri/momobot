package net.mauhiz.irc.base.data.defaut;

import java.util.Collection;
import java.util.Collections;

import net.mauhiz.irc.base.data.AbstractIrcNetwork;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.NumericReplies;
import net.mauhiz.irc.base.msg.ServerMsg;

/**
 * @author mauhiz
 * 
 */
public class DefaultServer extends AbstractIrcNetwork {

    public DefaultServer(String alias) {
        super(alias);
    }

    @Override
    public int getLineMaxLength() {
        return 127; // TODO confirm?
    }

    @Override
    public Collection<String> getServiceNicks() {
        return Collections.emptySet();
    }

    @Override
    public void handleSpecific(ServerMsg message, NumericReplies reply) {
        LOG.warn("Unhandled server reply : " + message);
    }

    /**
     * @see net.mauhiz.irc.base.data.AbstractIrcNetwork#newChannel(java.lang.String)
     */
    @Override
    public IrcChannel newChannel(String chanLowerCase) {
        return new DefaultChannel(chanLowerCase);
    }

    /**
     * @see net.mauhiz.irc.base.data.AbstractIrcNetwork#newUser(java.lang.String)
     */
    @Override
    public IrcUser newUser(String nick) {
        return new DefaultUser(nick);
    }
}
