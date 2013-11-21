package net.mauhiz.irc.base.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.util.Hooks;

/**
 * @author mauhiz
 */
public abstract class AbstractIrcChannel implements IrcChannel {
    /**
     * nom du chan sans le prefixe.
     */
    private final String nom;
    /**
     * prefixe du chan (#, &amp;)
     */
    private final String prefix;
    /**
     * flags du chan
     */
    protected IChannelProperties props;

    /**
     * users sur le channel
     */
    protected Map<IrcUser, Set<UserChannelMode>> users = new ConcurrentHashMap<>();

    /**
     * @param chanName
     */
    protected AbstractIrcChannel(String chanName) {
        prefix = chanName.substring(0, 1);
        nom = chanName.substring(1);
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcChannel#add(net.mauhiz.irc.base.data.IrcUser)
     */
    @Override
    public void add(IrcUser truite) {
        users.put(truite, new HashSet<UserChannelMode>());
    }

    @Override
    public int compareTo(IrcChannel o) {
        return fullName().compareTo(o.fullName());
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcChannel#contains(net.mauhiz.irc.base.data.IrcUser)
     */
    @Override
    public boolean contains(IrcUser smith) {
        return users.containsKey(smith);
    }

    /**
     * @see java.util.AbstractSet#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof IrcChannel)) {
            return false;
        }
        return fullName().equals(((IrcChannel) o).fullName());
    }

    @Override
    public String fullName() {
        return prefix + nom;
    }

    /**
     * @return running event
     */
    @Override
    public IChannelEvent getEvt() {
        return Hooks.getHook(this, IChannelEvent.class);
    }

    @Override
    public String getIrcForm() {
        return fullName();
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcChannel#getModes(IrcUser)
     */
    @Override
    public Set<UserChannelMode> getModes(IrcUser smith) {
        return users.get(smith);
    }

    /**
     * @return {@link #props}
     */
    @Override
    public IChannelProperties getProperties() {
        return props;
    }

    /**
     * @see java.util.AbstractSet#hashCode()
     */
    @Override
    public int hashCode() {
        return fullName().hashCode();
    }

    @Override
    public boolean isTopicEditable(IrcUser user) {
        return contains(user) && (!props.isOpTopic() || getModes(user).contains(UserChannelMode.OP));
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<IrcUser> iterator() {
        return users.keySet().iterator();
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcChannel#remove(net.mauhiz.irc.base.data.IrcUser)
     */
    @Override
    public void remove(IrcUser toRemove) {
        users.remove(toRemove);
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcChannel#size()
     */
    @Override
    public int size() {
        return users.size();
    }

    /**
     * @return msg
     */
    @Override
    public String stopEvent() {
        IChannelEvent localEvent = getEvt();
        if (localEvent == null) {
            return "";
        }
        Hooks.remove(localEvent);
        return localEvent.stop();
    }

    @Override
    public String toString() {
        return fullName();
    }
}
