package net.mauhiz.irc.bot.triggers.spam;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class SpamTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     */
    public SpamTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        /* TODO cross server */
        ArgumentList args = getArgs(im);
        String targetNick = args.poll();
        String msg = args.getRemainingData();
        IrcUser target = im.getServerPeer().getNetwork().findUser(targetNick, false);
        Privmsg spamMsg = new Privmsg(im.getServerPeer(), null, target, msg);
        long delay = 150;
        SpamRunnable spam = new SpamRunnable(spamMsg, control, delay);
        spam.launch();
    }
}
