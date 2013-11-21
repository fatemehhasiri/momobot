package net.mauhiz.irc.bot.triggers.base;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Join;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.trigger.IJoinTrigger;

/**
 * @author mauhiz
 */
public class WelcomeTrigger implements IJoinTrigger {
    /**
     * @see net.mauhiz.irc.base.trigger.IJoinTrigger#doTrigger(Join, IIrcControl)
     */
    @Override
    public void doTrigger(Join im, IIrcControl control) {
        IrcUser joiner = im.getFrom();
        if (!joiner.equals(im.getServerPeer().getMyself())) {
            for (IrcChannel channel : im.getChans()) {
                Notice notice = new Notice(im.getServerPeer(), null, channel, "Bienvenue sur " + channel);
                control.sendMsg(notice);
            }
        }
    }
}
