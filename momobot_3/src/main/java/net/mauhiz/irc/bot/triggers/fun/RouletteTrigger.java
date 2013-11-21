package net.mauhiz.irc.bot.triggers.fun;

import java.util.Iterator;
import java.util.Random;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Kick;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class RouletteTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {

    private static final Random RANDOM = new Random();

    /**
     * @param trigger
     */
    public RouletteTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel wannabe = (IrcChannel) im.getTo();
        if (wannabe == null) {
            /* look args to determine channels */
            ArgumentList args = getArgs(im);
            wannabe = im.getServerPeer().getNetwork().findChannel(args.poll());
            if (wannabe == null) {
                return;
            }
        }
        int index = RANDOM.nextInt(wannabe.size());
        Iterator<IrcUser> uIter = wannabe.iterator();
        for (int i = 0; i < index; i++) {
            uIter.next();
        }
        IrcUser random = uIter.next();
        Kick kick = new Kick(im.getServerPeer(), null, wannabe, random, "I, I know, how I feel when I'm around you");
        control.sendMsg(kick);
    }
}
