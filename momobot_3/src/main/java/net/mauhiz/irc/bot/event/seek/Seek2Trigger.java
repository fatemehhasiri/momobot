package net.mauhiz.irc.bot.event.seek;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.Gather;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author Topper
 */
public class Seek2Trigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public Seek2Trigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel chan = (IrcChannel) im.getTo();
        IChannelEvent event = chan.getEvt();
        ArgumentList args = getArgs(im);
        if (event == null) {
            int nbPlayers;
            try {
                nbPlayers = Integer.parseInt(args.poll());
                if (nbPlayers < 0) {
                    nbPlayers = 5;
                    throw new NumberFormatException("Integer <0");
                }

            } catch (NumberFormatException e) {
                Privmsg resp = new Privmsg(im, getTriggerHelp(), false);
                control.sendMsg(resp);
                return;
            }
            SeekWar2 skwar = new SeekWar2(chan, control, im.getServerPeer(), nbPlayers, args);
            Privmsg resp = new Privmsg(im, skwar.toString(), false);
            control.sendMsg(resp);
        } else if (event instanceof Gather) {
            Gather gather = (Gather) event;
            SeekWar2 skwar = new SeekWar2(chan, control, im.getServerPeer(), gather.getNumberPlayers(), args);
            Privmsg resp = new Privmsg(im, skwar.toString(), false);
            control.sendMsg(resp);

        } else {
            String msg = "Un " + event.getClass().getSimpleName() + " est deja lance sur " + im.getTo();
            Privmsg resp = new Privmsg(im, msg, false);
            control.sendMsg(resp);
        }
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <players-number> <level>";
    }
}
