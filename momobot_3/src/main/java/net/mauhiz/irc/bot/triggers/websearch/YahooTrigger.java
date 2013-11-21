package net.mauhiz.irc.bot.triggers.websearch;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class YahooTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public YahooTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        WebQuery query = new WebQuery("yahoo", getTriggerContent(cme));
        for (String next : query.results()) {
            Notice notice = new Notice(cme, next, true);
            control.sendMsg(notice);
        }
    }
}
