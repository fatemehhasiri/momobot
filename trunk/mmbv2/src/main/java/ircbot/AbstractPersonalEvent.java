package ircbot;

import utils.AbstractRunnable;

/**
 * Cette classe est horriblement moche. TODO virer cette �num qui sert � rien.
 * @author mauhiz
 */
public abstract class AbstractPersonalEvent extends AbstractRunnable {
    /**
     * @author mauhiz
     */
    protected enum ETAT {
        /**
         * �tat1.
         */
        ETAT1,
        /**
         * �tat2.
         */
        ETAT2,
        /**
         * �tat3.
         */
        ETAT3,
        /**
         * �teint.
         */
        OFF;
    }

    /**
     * le temps d'attente dans un thread.
     */
    protected static final long SLEEPTIME = 1000;
    /**
     * l'�tat, off par d�faut.
     */
    private ETAT                etat      = ETAT.OFF;
    /**
     * L'user associ�.
     */
    private final IrcUser       user;

    /**
     * @param user1
     *            l'user
     */
    public AbstractPersonalEvent(final IrcUser user1) {
        super();
        this.user = user1;
        user1.registerAutomate(this);
    }

    /**
     * @return le etat
     */
    public final ETAT getEtat() {
        return this.etat;
    }

    /**
     * @return le user
     */
    public final IrcUser getUser() {
        return this.user;
    }

    /**
     * @param etat1
     *            l'�tat
     */
    public final void setEtat(final ETAT etat1) {
        this.etat = etat1;
    }
}
