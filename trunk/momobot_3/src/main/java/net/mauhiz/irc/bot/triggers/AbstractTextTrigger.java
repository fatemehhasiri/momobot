package net.mauhiz.irc.bot.triggers;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public abstract class AbstractTextTrigger implements ITextTrigger {
    /**
     * 
     */
    private static final Logger LOG = Logger.getLogger(AbstractTextTrigger.class);
    /**
     * si le trigger est actif.
     */
    private boolean actif;
    /**
     * le texte du trigger.
     */
    private final String triggerText;
    
    /**
     * @param trigger
     *            le trigger
     */
    public AbstractTextTrigger(final String trigger) {
        triggerText = trigger;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof AbstractTextTrigger)) {
            return false;
        }
        return getClass().equals(obj.getClass()) && triggerText.equals(((AbstractTextTrigger) obj).triggerText);
    }
    
    /**
     * @param message
     *            le message
     * @return les params du msg
     */
    public final String getArgs(final String message) {
        return StringUtils.substringAfter(message, triggerText).trim();
    }
    
    /**
     * @see net.mauhiz.irc.bot.triggers.ITextTrigger#getTriggerHelp()
     */
    public final String getTriggerHelp() {
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
     * @return le actif
     */
    protected final boolean isActif() {
        return actif;
    }
    
    /**
     * @see net.mauhiz.irc.bot.triggers.ITextTrigger#isActivatedBy(java.lang.String)
     */
    public boolean isActivatedBy(final String msg) {
        if (msg == null) {
            return false;
        }
        String lcMsg = msg.toLowerCase(Locale.FRANCE);
        boolean activated = lcMsg.equals(triggerText) || lcMsg.startsWith(triggerText + " ");
        if (activated) {
            LOG.debug("Trigger " + getClass().getSimpleName() + " activated");
        }
        return activated;
    }
    
    /**
     * @param actif1
     *            le actif � r�gler
     */
    protected final void setActif(final boolean actif1) {
        actif = actif1;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return triggerText;
    }
}
