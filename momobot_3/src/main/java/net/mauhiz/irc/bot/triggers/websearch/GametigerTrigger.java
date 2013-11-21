package net.mauhiz.irc.bot.triggers.websearch;

import java.util.List;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class GametigerTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public GametigerTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        WebQuery query = new WebQuery("gametiger", getTriggerContent(cme));
        List<String> resultats = query.results();
        if (resultats.isEmpty()) {
            Notice notice = new Notice(cme, "rien trouve :/", true);
            control.sendMsg(notice);
            return;
        }
        for (String next : resultats) {
            Notice notice = new Notice(cme, next, true);
            control.sendMsg(notice);
        }
    }
}
