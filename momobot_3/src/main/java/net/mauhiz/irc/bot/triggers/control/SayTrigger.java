package net.mauhiz.irc.bot.triggers.control;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IAdminTrigger;

/**
 * @author mauhiz
 */
public class SayTrigger extends AbstractTextTrigger implements IAdminTrigger, IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public SayTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg pme, IIrcControl control) {
        ArgumentList args = getArgs(pme);
        String targetNick = args.poll();
        String message = args.getRemainingData();

        if (targetNick == null || message.length() == 0) {
            control.sendMsg(new Privmsg(pme, getTriggerHelp()));
        } else {
            /* TODO say cross-server ? */
            IrcUser target = pme.getServerPeer().getNetwork().findUser(targetNick, false);
            Privmsg msg = new Privmsg(pme.getServerPeer(), null, target, message);
            control.sendMsg(msg);
        }
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <target> <message>";
    }
}
