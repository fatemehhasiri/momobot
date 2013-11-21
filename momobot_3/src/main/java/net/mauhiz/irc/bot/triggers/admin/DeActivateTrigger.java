package net.mauhiz.irc.bot.triggers.admin;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.base.trigger.ITriggerManager;
import net.mauhiz.irc.bot.MmbTriggerManager;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IAdminTrigger;

/**
 * @author mauhiz
 */
public class DeActivateTrigger extends AbstractTextTrigger implements IAdminTrigger, IPrivmsgTrigger {

    /**
     * @param trigger
     */
    public DeActivateTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg pme, IIrcControl control) {
        ArgumentList args = getArgs(pme);
        if (args.isEmpty()) {
            showHelp(control, pme);
        } else {
            String className = args.poll();
            LOG.info("Deactivating trigger class: " + className);
            ITriggerManager[] managers = control.getManagers();
            for (ITriggerManager manager : managers) {
                if (manager instanceof MmbTriggerManager) {

                    if (args.isEmpty()) {
                        /* remove inconditionnel */
                        ((MmbTriggerManager) manager).removeTrigger(className, null);
                    } else {
                        /* remove de certains triggertext seulement */
                        ((MmbTriggerManager) manager).removeTrigger(className, args.asList());
                    }
                }
            }
        }
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <class>";
    }
}
