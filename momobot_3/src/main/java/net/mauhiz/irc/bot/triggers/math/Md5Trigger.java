package net.mauhiz.irc.bot.triggers.math;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Calcule un md5.
 * 
 * @author mauhiz
 */
public class Md5Trigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     */
    public Md5Trigger(String trigger) {
        super(trigger);
    }

    /**
     * MessageDigest
     * 
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        String content = getTriggerContent(cme);

        if (StringUtils.isEmpty(content)) {
            showHelp(control, cme);
        } else {
            String resp = "md5 de " + content + ": " + DigestUtils.md5Hex(content);
            Privmsg msg = new Privmsg(cme, resp);
            control.sendMsg(msg);
        }
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <data>";
    }
}
