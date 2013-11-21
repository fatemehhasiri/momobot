package net.mauhiz.irc.bot.triggers.base;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.IrcClientControl;
import net.mauhiz.irc.base.msg.IPrivateIrcMessage;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.DefaultTriggerManager;
import net.mauhiz.irc.base.trigger.INoticeTrigger;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.base.trigger.ITextTrigger;
import net.mauhiz.irc.base.trigger.ITrigger;
import net.mauhiz.irc.base.trigger.ITriggerManager;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.ICommand;
import net.mauhiz.util.Messages;

/**
 * @author mauhiz
 */
public class HelpTrigger extends AbstractTextTrigger implements IPrivmsgTrigger, INoticeTrigger {

    /**
     * @param control
     * @return triggers view
     */
    private static Iterable<ITrigger> getTriggers(IIrcControl control) {
        IrcClientControl realControl = (IrcClientControl) control;
        ITriggerManager[] managers = realControl.getManagers();
        for (ITriggerManager manager : managers) {
            if (manager instanceof DefaultTriggerManager) {
                return manager.getTriggers();
            }
        }
        return Collections.emptySet();
    }

    private static SortedSet<String> listCmds(IIrcControl control, Class<? extends ITextTrigger> trigIface) {
        SortedSet<String> cmds = new TreeSet<>();
        for (ITrigger trig : getTriggers(control)) {
            if (trig instanceof ICommand && trigIface.isInstance(trig)) {
                cmds.add(((ICommand) trig).getTriggerText());
            }
        }
        return cmds;
    }

    /**
     * @param trigger
     *            le trigger
     */
    public HelpTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.INoticeTrigger#doTrigger(Notice, IIrcControl)
     */
    @Override
    public void doTrigger(Notice im, IIrcControl control) {
        SortedSet<String> cmds = listCmds(control, INoticeTrigger.class);
        int maxLen = im.getMaxPayload();

        sendNoticeHelp(control, im, cmds, maxLen);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        SortedSet<String> cmds = listCmds(control, IPrivmsgTrigger.class);
        int maxLen = im.getMaxPayload();
        sendPrivmsgHelp(control, im, cmds, maxLen);
    }

    private String getHeader() {
        return Messages.get(getClass(), "help"); //$NON-NLS-1$
    }

    private void sendNoticeHelp(IIrcControl control, Notice replyTo, SortedSet<String> cmds, int maxLen) {
        StringBuilder msg = new StringBuilder(getHeader());
        for (String trig : cmds) {
            if (msg.length() >= maxLen) { // flush
                Notice resp = new Notice(replyTo, msg.toString(), true);
                control.sendMsg(resp);
                msg = new StringBuilder(getHeader());
            }
            msg.append(trig).append(' ');
        }
        Notice resp = new Notice(replyTo, msg.toString(), true);
        control.sendMsg(resp);
    }

    protected void sendPrivmsgHelp(IIrcControl control, IPrivateIrcMessage replyTo, SortedSet<String> cmds, int maxLen) {
        StringBuilder msg = new StringBuilder(getHeader());
        for (String trig : cmds) {
            if (msg.length() + trig.length() + 1 >= maxLen) {
                Privmsg resp = new Privmsg(replyTo, msg.toString());
                control.sendMsg(resp);
                msg = new StringBuilder(getHeader());
            }
            msg.append(trig).append(' ');
        }
        Privmsg resp = new Privmsg(replyTo, msg.toString());
        control.sendMsg(resp);
    }
}
