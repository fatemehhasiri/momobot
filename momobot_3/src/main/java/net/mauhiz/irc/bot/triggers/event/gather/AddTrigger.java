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
public class AddTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public AddTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel chan = (IrcChannel) im.getTo();
        IChannelEvent event = chan.getEvt();
        if (event == null) {
            Privmsg msg = new Privmsg(im, "Aucun gather ou pickup n'est lance.");
            control.sendMsg(msg);
            return;
        }
        IrcUser user = (IrcUser) im.getFrom();
        if (event instanceof Gather) {
            Privmsg msg = new Privmsg(im, ((Gather) event).add(user));
            control.sendMsg(msg);
        } else if (event instanceof Pickup) {
            Privmsg msg = new Privmsg(im, ((Pickup) event).add(user, getTriggerContent(im)));
            control.sendMsg(msg);
        }
    }
}
