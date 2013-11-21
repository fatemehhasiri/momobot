package net.mauhiz.irc.base.data;

import java.net.URI;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.mauhiz.irc.MomoStringUtils;

import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public abstract class AbstractIrcNetwork implements IrcNetwork {

    protected static final Logger LOG = Logger.getLogger(AbstractIrcNetwork.class);
    private final String alias;
    /**
     * a chaque server sa liste de channels.
     */
    private final SortedSet<IrcChannel> channels = new TreeSet<>();

    private URI defaultUri;
    /**
     * a chaque server sa liste d users.
     */
    private final SortedSet<IrcUser> users = new TreeSet<>();

    protected AbstractIrcNetwork(String alias) {
        super();
        this.alias = alias;
    }

    @Override
    public int countUsers() {
        return users.size();
    }

    @Override
    public IrcChannel findChannel(String chanName) {
        return findChannel(chanName, true);
    }

    @Override
    public IrcChannel findChannel(String chanName, boolean addIfNotFound) {
        //
        for (IrcChannel chan : channels) {
            if (chan.fullName().equalsIgnoreCase(chanName)) {
                return chan;
            }
        }
        if (addIfNotFound && MomoStringUtils.isChannelName(chanName)) {
            String chanLowerCase = chanName.toLowerCase(Locale.ENGLISH);
            IrcChannel chan = newChannel(chanLowerCase);
            channels.add(chan);
            return chan;
        }

        return null;
    }

    @Override
    public IrcUser findUser(HostMask mask, boolean addIfNotFound) {
        String nick = mask.getNick();
        if (nick == null) {
            throw new IllegalArgumentException("Invalid mask (no nick): " + mask);
        }
        for (IrcUser user : users) {
            if (nick.equalsIgnoreCase(user.getNick())) {
                user.setMask(mask);
                return user;
            }
        }
        if (addIfNotFound) {
            IrcUser newUser = newUser(mask.getNick());
            newUser.setMask(mask);
            users.add(newUser);
            return newUser;
        }
        return null;
    }

    @Override
    public IrcUser findUser(String nick, boolean addIfNotFound) {
        if (nick.contains("!") || nick.contains(" ")) { // TODO better regexp
            throw new IllegalArgumentException("invalid nick : " + nick);
        }
        for (IrcUser user : users) {
            if (nick.equalsIgnoreCase(user.getNick())) {
                return user;
            }
        }

        IrcUser newUser = newUser(nick);
        if (addIfNotFound) {
            users.add(newUser);
        }
        return newUser;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Iterable<IrcChannel> getChannels() {
        return channels;
    }

    @Override
    public Set<IrcChannel> getChannelsForUser(IrcUser smith) {
        Set<IrcChannel> chans = new HashSet<>();
        for (IrcChannel chan : channels) {
            if (chan.contains(smith)) {
                chans.add(chan);
            }
        }
        return chans;
    }

    @Override
    public int getLineMaxLength() {
        return 512;
    }

    public IrcNetwork getNetwork() {
        return this;
    }

    /**
     * @return users
     */
    public Iterable<IrcUser> getUsers() {
        return users;
    }

    @Override
    public final IIrcServerPeer newServerPeer() {
        Objects.requireNonNull(defaultUri);
        return new IrcServerPeer(this, defaultUri);
    }

    @Override
    public final IIrcServerPeer newServerPeer(URI uri) {
        return new IrcServerPeer(this, uri);
    }

    /**
     * @param channel
     */
    @Override
    public void remove(IrcChannel channel) {
        channels.remove(channel);
    }

    @Override
    public void remove(IrcUser quitter) {
        users.remove(quitter);
    }

    @Override
    public void setDefaultUri(URI defaultUri) {
        this.defaultUri = defaultUri;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getAlias();
    }

    @Override
    public void updateNick(IrcUser oldUser, String newNick) {
        if (oldUser == null) {
            /* we did not know him anyways. how so? */
            findUser(newNick, true);
        } else {
            oldUser.setNick(newNick);
        }
    }
}
