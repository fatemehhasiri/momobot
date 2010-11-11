package net.mauhiz.irc.bot.triggers.base;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IPrivmsgTrigger;
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
        Privmsg retour = Privmsg.buildAnswer(im,
                Messages.get(getClass(), "count.users", Integer.valueOf(im.getServer().countUsers()))); //$NON-NLS-1$
        control.sendMsg(retour);
    }
}
