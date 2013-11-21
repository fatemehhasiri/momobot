package net.mauhiz.irc.bot.triggers.base;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.Messages;

/**
 * @author mauhiz
 */
public class CountUsersTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     */
    public CountUsersTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        /* TODO cross server */
        Privmsg retour = new Privmsg(im, Messages.get(getClass(),
                "count.users", Integer.valueOf(im.getServerPeer().getNetwork().countUsers()))); //$NON-NLS-1$
        control.sendMsg(retour);
    }
}
