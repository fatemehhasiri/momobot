package net.mauhiz.irc.bot.event.seek;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mauhiz.irc.base.data.IrcUser;

/**
 * @author Topper
 */
public class SeekUserHistory implements Iterable<String> {
    private final List<String> history = new ArrayList<>();
    private SeekStatus status;
    private final IrcUser user;

    /**
     * @param user
     */
    public SeekUserHistory(IrcUser user) {
        this(user, SeekStatus.STARTING);
    }

    public SeekUserHistory(IrcUser user, SeekStatus status) {
        super();
        this.user = user;
        this.status = status;
    }

    public boolean add(String st) {
        return history.add(st);
    }

    /**
     * Renvoie un entier identifiant de maniere unique un utilisateur
     * 
     * @return {@link #status}
     */
    public SeekStatus getId() {
        return status;
    }

    /**
     * @return user.nick
     */
    public String getNick() {
        return user.getNick();
    }

    public IrcUser getUser() {
        return user;
    }

    @Override
    public Iterator<String> iterator() {
        return history.iterator();
    }

    /**
     * @param id1
     */
    public void setId(final SeekStatus id1) {
        status = id1;
    }
}
