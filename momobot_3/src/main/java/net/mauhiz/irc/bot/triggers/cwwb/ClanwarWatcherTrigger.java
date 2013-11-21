package net.mauhiz.irc.bot.triggers.cwwb;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Part;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.msg.Quit;
import net.mauhiz.irc.base.trigger.IPartTrigger;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.base.trigger.IQuitTrigger;
import net.mauhiz.util.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 * @author abby
 */
public class ClanwarWatcherTrigger implements IPrivmsgTrigger, IPartTrigger, IQuitTrigger {
    /**
     * nicks ignores.
     */
    private static final String[] IGNORED_NICKS = { "[CW|FR]", "Q", "S" };

    /**
     */
    public ClanwarWatcherTrigger() {
        super();

        // On vide les tables direct en arrivant
        // TODO permettre un parametrage de cette fonction ?
        SQLQuery trunc = HibernateUtils.currentSession().createSQLQuery("truncate table WAR");
        trunc.executeUpdate();
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPartTrigger#doTrigger(Part, IIrcControl)
     */
    @Override
    public void doTrigger(Part im, IIrcControl control) {
        removeSeekFromUser(im.getFrom());
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        String message = im.getMessage();
        // On ignore les messages trop courts pour etre utiles
        if (message.length() <= 3) {
            return;
        }

        IrcUser sender = (IrcUser) im.getFrom();

        // On ignore les messages provenant de certains utilisateurs.
        for (String element : IGNORED_NICKS) {
            if (element.equals(sender.getNick())) {
                return;
            }
        }

        // Commande de parsage des wars
        processMessage(sender, message);

    }

    /**
     * @see net.mauhiz.irc.base.trigger.IQuitTrigger#doTrigger(Quit, IIrcControl)
     */
    @Override
    public void doTrigger(Quit im, IIrcControl control) {
        removeSeekFromUser((IrcUser) im.getFrom());
    }

    /**
     * @see net.mauhiz.irc.base.trigger.ITextTrigger#isActivatedBy(java.lang.String)
     */
    @Override
    public boolean isActivatedBy(String text) {
        return true;
    }

    /**
     * @param sender
     * @param pMessage
     */
    public void processMessage(IrcUser sender, String pMessage) {
        String message = pMessage.trim();
        // String lowMsg = message.toLowerCase();

        // TODO est-ce que l'objet war est bien supprime a chaque fois ?
        War war = new War(sender, message);

        // Si le parser n'a pas reussi a trouver une war on arrete

        HibernateUtils.currentSession().getTransaction().begin();
        if (war.isNull()) {
            /*
             * TODO Rajouter une option debug ou pas : pour faire une version du bot plus legere.
             */
            Untreated utt = new Untreated();
            utt.setMessage(message);
            utt.setUser(sender);
            HibernateUtils.currentSession().save(utt);
        } else {
            /*
             * Insertion du message dans la bdd. On insert le nouveau seek, en remplacant l'ancien si un seek du meme
             * joueur existe deja
             */
            Query qry = HibernateUtils.currentSession().createQuery("from War where user = :user");
            qry.setString("user", sender.getNick());
            War existing = (War) qry.uniqueResult();
            if (existing != null) {
                war.setId(existing.getId());
                HibernateUtils.currentSession().delete(existing);
            }
            HibernateUtils.currentSession().save(war);
        }
        HibernateUtils.currentSession().getTransaction().commit();
    }

    /**
     * Enleve les seeks des user ayant quit clanwar de la bdd
     * 
     * @param user
     */
    public void removeSeekFromUser(IrcUser user) {
        SQLQuery qry = HibernateUtils.currentSession().createSQLQuery("delete from War where user = :user");
        qry.setString("user", user.getNick());
        qry.executeUpdate();
    }
}
