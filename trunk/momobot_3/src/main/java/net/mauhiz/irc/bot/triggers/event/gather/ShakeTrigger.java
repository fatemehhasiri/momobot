package net.mauhiz.irc.bot.triggers.event.gather;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.bot.event.ChannelEvent;
import net.mauhiz.irc.bot.event.Pickup;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IPrivmsgTrigger;

/**
 * @author Topper
 */
public class ShakeTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    
    /**
     * @param trigger
     *            le trigger
     */
    
    public ShakeTrigger(String trigger) {
        super(trigger);
    }
    
    /**
     * @see net.mauhiz.irc.bot.triggers.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel chan = im.getServer().findChannel(im.getTo());
        ChannelEvent evt = chan.getEvt();
        String reply;
        if (evt == null) {
            reply = "Aucun gather n'est lance.";
        } else {
            if (evt instanceof Pickup) {
                /* On shake */
                reply = ((Pickup) evt).shake();
            } else {
                return;
            }
        }
        Privmsg msg = Privmsg.buildAnswer(im, reply);
        control.sendMsg(msg);
    }
}
