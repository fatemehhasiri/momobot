package net.mauhiz.irc.bot.event.seek;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

public class StopSeek2Trigger extends AbstractTextTrigger implements IPrivmsgTrigger {

    /**
     * @param trigger
     */
    public StopSeek2Trigger(String trigger) {
        super(trigger);
    }

    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel chan = (IrcChannel) im.getTo();
        IChannelEvent evt = chan.getEvt();
        String reply = "";
        if (evt == null) {
            reply = "Aucun gather n'est lance.";
        } else {
            if (evt instanceof SeekWar2) {
                if (((SeekWar2) evt).isExpired()) {
                    reply = "Le seek n'est pas lance.";
                } else {
                    // if (((Gather) evt).getSeek().isSeekInProgress()) {
                    ((SeekWar2) evt).setStop("Arret demande");
                    // }
                }

            } else {
                return;
            }
        }
        Privmsg msg = new Privmsg(im, reply, false);
        control.sendMsg(msg);
    }
}
