package net.mauhiz.irc.bot.triggers.admin;

import java.util.Arrays;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.base.trigger.ITriggerManager;
import net.mauhiz.irc.bot.MmbTriggerManager;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IAdminTrigger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

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
        String[] args = StringUtils.split(getArgs(pme.getMessage()));
        if (ArrayUtils.isEmpty(args)) {
            Privmsg retour = Privmsg.buildPrivateAnswer(pme, "you need to specify Trigger class name");
            Privmsg retour2 = Privmsg.buildPrivateAnswer(pme, "for instance : " + this + " " + getClass().getName());
            control.sendMsg(retour);
            control.sendMsg(retour2);
        } else {
            String className = args[0];
            LOG.info("Deactivating trigger class: " + className);
            ITriggerManager[] managers = control.getManagers();
            for (ITriggerManager manager : managers) {
                if (manager instanceof MmbTriggerManager) {

                    if (args.length == 1) {
                        /* remove inconditionnel */
                        ((MmbTriggerManager) manager).removeTrigger(className);
                    } else {
                        /* remove de certains triggertext seulement */
                        String[] texts = Arrays.copyOfRange(args, 1, args.length);
                        ((MmbTriggerManager) manager).removeTrigger(className, texts);
                    }
                }
            }
        }
    }
}
