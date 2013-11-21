package net.mauhiz.irc.base.data;

import java.util.Objects;

/**
 * @author mauhiz
 */
public abstract class AbstractIrcUser implements IrcUser {
    private String fullName;
    private HostMask mask;
    private String nick;
    protected UserProperties props;

    /**
     * @param nick1
     */
    protected AbstractIrcUser(String nick1) {
        nick = nick1;
    }

    @Override
    public int compareTo(IrcUser o) {
        return nick.compareTo(o.getNick());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof IrcUser)) {
            return false;
        }
        return nick.equals(((IrcUser) obj).getNick());
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcUser#getFullName()
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getIrcForm() {
        return mask == null ? nick : mask.getIrcForm();
    }

    @Override
    public HostMask getMask() {
        if (mask == null) {
            mask = HostMask.getInstance(nick + "!*@*");
        }
        return mask;
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcUser#getNick()
     */
    @Override
    public String getNick() {
        return nick;
    }

    @Override
    public UserProperties getProperties() {
        return props;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return nick.hashCode();
    }

    /**
     * Par defaut : pas de service
     * 
     * @see net.mauhiz.irc.base.data.IrcUser#isService()
     */
    @Override
    public boolean isService() {
        return false;
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcUser#setFullName(java.lang.String)
     */
    @Override
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public void setMask(HostMask mask) {
        this.mask = mask;
        setNick(mask.getNick());
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcUser#setNick(java.lang.String)
     */
    @Override
    public void setNick(String nick) {
        Objects.requireNonNull(nick);
        this.nick = nick;
    }

    @Override
    public String toString() {
        return nick;
    }
}
