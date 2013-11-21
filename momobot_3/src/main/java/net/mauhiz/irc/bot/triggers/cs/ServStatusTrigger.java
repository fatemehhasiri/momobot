package net.mauhiz.irc.bot.triggers.cs;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.NetUtils;

/**
 * @author mauhiz
 */
public class ServStatusTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public ServStatusTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        String args = getTriggerContent(im);
        IServer server = new Server(NetUtils.makeISA(args));
        String reply = server.toString();
        Privmsg msg = new Privmsg(im, reply);
        control.sendMsg(msg);
    }
}
