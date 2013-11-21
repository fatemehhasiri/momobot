package net.mauhiz.irc.bot.triggers.control;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IAdminTrigger;

/**
 * @author mauhiz
 */
public class NoticeTrigger extends AbstractTextTrigger implements IAdminTrigger, IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public NoticeTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        ArgumentList args = getArgs(im);
        String targetNick = args.poll();
        String message = args.getRemainingData();

        if (targetNick == null || message.length() == 0) {
            Privmsg msg = new Privmsg(im, "pas assez de parametres. " + getTriggerHelp(), true);
            control.sendMsg(msg);
        } else {
            IrcUser target = im.getServerPeer().getNetwork().findUser(targetNick, false);

            /* FIXME cross-server */
            Notice msg = new Notice(im.getServerPeer(), null, target, message);
            control.sendMsg(msg);
        }
    }

    @Override
    public String getTriggerHelp() {
        return "syntaxe $" + getTriggerText() + " target msg";
    }
}
