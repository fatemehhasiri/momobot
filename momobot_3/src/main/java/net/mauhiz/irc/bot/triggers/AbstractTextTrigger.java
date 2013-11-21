package net.mauhiz.irc.bot.triggers;

import java.util.Locale;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.msg.IPrivateIrcMessage;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.trigger.ITextTrigger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public abstract class AbstractTextTrigger implements ITextTrigger, ICommand {
    /**
     * logger.
     */
    protected static final Logger LOG = Logger.getLogger(ITextTrigger.class);
    /**
     * le texte du trigger.
     */
    private final String triggerText;

    /**
     * @param trigger
     *            le trigger
     */
    public AbstractTextTrigger(String trigger) {
        triggerText = trigger;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof AbstractTextTrigger)) {
            return false;
        }
        return getClass().equals(obj.getClass()) && triggerText.equals(((AbstractTextTrigger) obj).getTriggerText());
    }

    protected ArgumentList getArgs(IPrivateIrcMessage im) {
        return new ArgumentList(getTriggerContent(im));
    }

    /**
     * @param im
     *            le message
     * @return les params du msg
     */
    protected String getTriggerContent(IPrivateIrcMessage im) {
        return im == null ? null : StringUtils.substringAfter(im.getMessage(), triggerText).trim();
    }

    /**
     * @see net.mauhiz.irc.bot.triggers.ICommand#getTriggerHelp()
     */
    @Override
    public String getTriggerHelp() {
        return "Usage: " + triggerText;
    }

    /**
     * @return {@link #triggerText}
     */
    @Override
    public String getTriggerText() {
        return triggerText;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getClass()).append(triggerText).toHashCode();
    }

    /**
     * @see net.mauhiz.irc.base.trigger.ITextTrigger#isActivatedBy(java.lang.String)
     */
    @Override
    public boolean isActivatedBy(String msg) {
        if (msg == null) {
            return false;
        }
        String lcMsg = msg.toLowerCase(Locale.FRANCE);
        boolean activated = lcMsg.equals(triggerText) || lcMsg.startsWith(triggerText + ' ');
        if (activated) {
            LOG.debug("Trigger " + getClass().getSimpleName() + " activated");
        }
        return activated;
    }

    protected void showHelp(IIrcControl control, IPrivateIrcMessage replyTo) {
        control.sendMsg(new Notice(replyTo, getTriggerHelp(), true));
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return triggerText;
    }
}
