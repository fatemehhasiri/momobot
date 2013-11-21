package net.mauhiz.irc.bot.triggers.base;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mauhiz
 */
public class UsersTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {

    /**
     * @param trigger
     */
    public UsersTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        ArgumentList chanNames = getArgs(im);
        if (chanNames.isEmpty()) {
            chanNames = new ArgumentList(((IrcChannel) im.getTo()).fullName());
        }

        for (String chanName : chanNames) {
            IrcChannel channel = im.getServerPeer().getNetwork().findChannel(chanName, false);
            if (channel == null) { // TODO /NAMES
                String chanMsg = "I am not on " + chanName;
                control.sendMsg(new Privmsg(im, chanMsg));

            } else {
                String chanMsg = channel.size() + " users on " + channel.fullName() + " : "
                        + StringUtils.join(channel.iterator(), " ");
                control.sendMsg(new Privmsg(im, chanMsg));
            }
        }
    }
}
