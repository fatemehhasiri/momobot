package net.mauhiz.irc.bot.triggers.fun;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

import org.apache.commons.lang3.StringUtils;

/**
 * En voila un trigger qu'il est useless.
 * 
 * @author mauhiz
 */
public class LengthTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     */
    public LengthTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        String args = getTriggerContent(cme);
        if (StringUtils.isBlank(args)) {
            return;
        }

        Privmsg msg = new Privmsg(cme, "longueur = " + args.length());
        control.sendMsg(msg);
    }
}
