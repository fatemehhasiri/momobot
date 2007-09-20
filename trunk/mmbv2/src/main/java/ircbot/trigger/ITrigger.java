package ircbot.trigger;


/**
 * @author mauhiz
 */
public interface ITrigger {
    /**
     * @return Un message d'aide; par d�faut, le texte du trigger.
     */
    String getTriggerText();
    
    /**
     * @param msg
     * @return le r�sultat du test
     */
    boolean isActivatedBy(final String msg);
}
