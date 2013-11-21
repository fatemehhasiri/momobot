package net.mauhiz.irc.bot.triggers.admin;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class AuthTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public AuthTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg pme, IIrcControl control) {
        /* pour l'instant je hard code le pw. La secu attendra ;x */
        if ("boulz".equals(getTriggerContent(pme))) {
            pme.getFrom()/* FIXME .setAdmin(true) */;
            Privmsg msg = new Privmsg(pme, "Oui, maitre", true);
            control.sendMsg(msg);
        }
    }
}
