package net.mauhiz.irc.bot.triggers.base;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Invite;
import net.mauhiz.irc.base.msg.Join;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.trigger.IInviteTrigger;
import net.mauhiz.util.Messages;

/**
 * @author mauhiz
 */
public class JoinOnInviteTrigger implements IInviteTrigger {

    /**
     * On est sympa, on join tout.
     */
    @Override
    public void doTrigger(Invite im, IIrcControl control) {
        Join join = new Join(im.getServerPeer(), im.getChan());
        control.sendMsg(join);
        Notice notice = new Notice(im.getServerPeer(), null, im.getChan(), Messages.get(getClass(), "join.on.invite")); //$NON-NLS-1$
        control.sendMsg(notice);
    }
}
