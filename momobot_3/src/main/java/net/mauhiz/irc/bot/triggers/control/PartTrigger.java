package net.mauhiz.irc.bot.triggers.control;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.msg.Part;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IAdminTrigger;

/**
 * @author mauhiz
 */
public class PartTrigger extends AbstractTextTrigger implements IPrivmsgTrigger, IAdminTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public PartTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        ArgumentList args = getArgs(im);
        String chan = args.poll();
        String reason = args.getRemainingData();

        /* TODO part cross servers */
        Part leave = new Part(im.getServerPeer(), reason, im.getServerPeer().getNetwork().findChannel(chan));
        control.sendMsg(leave);
    }
}
