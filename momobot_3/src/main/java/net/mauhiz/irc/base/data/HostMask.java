package net.mauhiz.irc.base.data;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mauhiz
 */
public class HostMask implements Target {
    private static final Pattern HOSTMASK_PATTERN = Pattern.compile("(.*)!(.*)@(.*)");

    public static HostMask getInstance(String raw) {
        Objects.requireNonNull(raw);

        Matcher m = HOSTMASK_PATTERN.matcher(raw);
        if (m.matches()) {
            HostMask mask = new HostMask();
            mask.nick = m.group(1);
            mask.user = m.group(2);
            mask.host = m.group(3);
            return mask;

        }

        return null;
    }

    private String host = "?";
    private String nick;
    private String user = "?";

    private HostMask() {
        super();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof HostMask)) {
            return false;
        }
        return getIrcForm().equals(((HostMask) obj).getIrcForm());
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    @Override
    public String getIrcForm() {
        return nick + "!" + user + "@" + host;
    }

    /**
     * @return {@link #nick}
     */
    public String getNick() {
        return nick;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getIrcForm().hashCode();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return getIrcForm();
    }
}
