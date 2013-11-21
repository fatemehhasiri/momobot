package net.mauhiz.irc.bot.triggers.control;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Join;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IAdminTrigger;

/**
 * @author mauhiz
 */
public class JoinTrigger extends AbstractTextTrigger implements IPrivmsgTrigger, IAdminTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public JoinTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        /* TODO join cross server */
        String chanName = getTriggerContent(im);
        Join go = new Join(im.getServerPeer(), im.getServerPeer().getNetwork().findChannel(chanName));
        control.sendMsg(go);
    }
}
