package net.mauhiz.irc.bot.triggers.base;

import java.util.Map.Entry;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IAdminTrigger;
import net.mauhiz.irc.bot.triggers.cs.PlayerDB;
import net.mauhiz.util.Messages;

/**
 * @author mauhiz
 */
public class KnownPlayersTrigger extends AbstractTextTrigger implements IPrivmsgTrigger, IAdminTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public KnownPlayersTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg pme, IIrcControl control) {
        String msg = Messages.get(getClass(), "known.players", Integer.valueOf(PlayerDB.countPlayers())); //$NON-NLS-1$
        Privmsg retour = new Privmsg(pme, msg, true);
        control.sendMsg(retour);
        for (Entry<String, String> item : PlayerDB.getPlayers()) {
            retour = new Privmsg(pme, item.getValue() + " - " + item.getKey(), true); //$NON-NLS-1$
            control.sendMsg(retour);
        }
    }
}
