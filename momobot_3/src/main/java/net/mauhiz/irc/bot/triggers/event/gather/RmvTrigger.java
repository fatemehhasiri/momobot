package net.mauhiz.irc.bot.triggers.event.gather;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.Gather;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.event.Pickup;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class RmvTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public RmvTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel chan = (IrcChannel) im.getTo();
        IChannelEvent evt = chan.getEvt();
        String reply;
        if (evt == null) {
            reply = "Aucun gather ou pickup n'est lance.";
        } else {
            IrcUser user = (IrcUser) im.getFrom();
            if (evt instanceof Gather) {
                reply = ((Gather) evt).remove(user);
            } else if (evt instanceof Pickup) {
                reply = ((Pickup) evt).remove(user);
            } else {
                return;
            }
        }
        Privmsg msg = new Privmsg(im, reply);
        control.sendMsg(msg);
    }
}
