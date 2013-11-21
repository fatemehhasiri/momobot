package net.mauhiz.irc.bot.triggers.websearch;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class GoogleTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public GoogleTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        WebQuery query = new WebQuery("google", getTriggerContent(cme));
        for (String next : query.results()) {
            IIrcMessage notice = new Privmsg(cme, next);
            control.sendMsg(notice);
        }
    }
}
