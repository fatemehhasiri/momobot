package net.mauhiz.irc.base.data;

import java.net.InetSocketAddress;
import java.net.URI;

import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class IrcServerPeer extends AbstractPeer implements IIrcServerPeer {

    protected static final Logger LOG = Logger.getLogger(IrcServerPeer.class);

    private String ircForm;
    private IrcUser myself;
    private final IrcNetwork network;

    /**
     * @param uri
     */
    protected IrcServerPeer(IrcNetwork network, URI uri) {
        super(new InetSocketAddress(uri.getHost(), uri.getPort()));
        this.network = network;
    }

    @Override
    public String getIrcForm() {
        return ircForm;
    }

    /**
     * @return the myself
     */
    @Override
    public IrcUser getMyself() {
        return myself;
    }

    @Override
    public IrcNetwork getNetwork() {
        return network;
    }

    @Override
    public IrcUser introduceMyself(String nick, String user, String fullName) {
        myself = network.newUser(nick);
        if (user != null) {
            myself.getMask().setUser(user);
        }

        myself.setFullName(fullName);
        return myself;
    }

    @Override
    public void setIrcForm(String ircForm) {
        this.ircForm = ircForm;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return myself + " on " + network;
    }
}
