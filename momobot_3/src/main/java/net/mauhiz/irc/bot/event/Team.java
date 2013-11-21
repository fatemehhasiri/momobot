package net.mauhiz.irc.bot.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.mauhiz.irc.base.data.IrcUser;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author mauhiz
 */
public class Team implements Iterable<IrcUser> {
    private static final Random RANDOM = new Random();
    private final int capacity;
    private final List<IrcUser> members;
    private String nom;

    /**
     * @param size1
     * @param nom1
     */
    public Team(int size1, String nom1) {
        super();
        capacity = size1;
        nom = nom1;
        members = new ArrayList<>(size1);
    }

    public boolean add(IrcUser a) {
        if (isFull()) {
            throw new IllegalStateException("Team is full!");
        }
        return members.add(a);
    }

    public void clear() {
        members.clear();
    }

    public boolean contains(IrcUser o) {
        return members.contains(o);
    }

    /**
     * @return {@link #capacity}.
     */
    public int getCapacity() {
        return capacity;
    }

    public Collection<IrcUser> getMembers() {
        return members;
    }

    /**
     * @return {@link #nom}.
     */
    public String getNom() {
        return nom;
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * @return si la team est complete.
     */
    public boolean isFull() {
        return remainingPlaces() <= 0;
    }

    @Override
    public Iterator<IrcUser> iterator() {
        return members.iterator();
    }

    public IrcUser randomPlayer() {
        return members.get(RANDOM.nextInt(size()));
    }

    /**
     * @return le nombre de places restantes.
     */
    public int remainingPlaces() {
        return capacity - size();
    }

    public boolean remove(IrcUser o) {
        return members.remove(o);
    }

    /**
     * @param nom1
     */
    public void setNom(String nom1) {
        nom = nom1;
    }

    public int size() {
        return members.size();
    }

    /**
     * debug only
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
