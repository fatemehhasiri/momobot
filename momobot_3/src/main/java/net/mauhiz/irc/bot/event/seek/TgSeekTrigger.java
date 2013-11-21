package net.mauhiz.irc.bot.event.seek;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.Gather;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class TgSeekTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public TgSeekTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel chan = (IrcChannel) im.getTo();
        IChannelEvent evt = chan.getEvt();
        String reply;
        if (evt == null) {
            reply = "Aucun gather n'est lance.";
        } else if (evt instanceof Gather) {
            if (((Gather) evt).getSeek() == null) {
                reply = "Pas de seek en cours.";

            } else if (((Gather) evt).getSeek().isSeekInProgress()) {
                reply = "Le seek est en cours.";

            } else { // on reset le seek
                ((Gather) evt).setSeekToNull();
                reply = "ok je remballe.";
            }
        } else {
            return;
        }

        Privmsg msg = new Privmsg(im, reply, false);
        control.sendMsg(msg);
    }
}
