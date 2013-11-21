package net.mauhiz.irc.base.trigger;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.MsgState;
import net.mauhiz.irc.base.msg.IIrcMessage;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class DefaultTriggerManager implements ITriggerManager {
    private static final Logger LOG = Logger.getLogger(DefaultTriggerManager.class);

    private static Class<? extends ITrigger> findClass(String trigClassFull) {
        try {
            Class<?> wannabe = Class.forName(trigClassFull);
            if (!ITrigger.class.isAssignableFrom(wannabe)) {
                LOG.warn("Not a trigger: " + wannabe.getName());
                return null;
            }
            return wannabe.asSubclass(ITrigger.class);
        } catch (ClassNotFoundException e) {
            LOG.warn(e);
            return null;
        }
    }

    /**
     * keeper of the seven keys
     */
    private final Set<ITrigger> myKeeper = new CopyOnWriteArraySet<>();

    /**
     * @param triggerClass
     * @param params
     */
    void addTrigger(Class<? extends ITrigger> triggerClass, Object... params) {
        try {
            ITrigger trigger = (ITrigger) ConstructorUtils.invokeConstructor(triggerClass, params);
            addTrigger(trigger);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.warn(e, e);
        }
    }

    public void addTrigger(ITrigger trigger) {
        if (!(trigger instanceof ITextTrigger)) {
            /* seuls les triggers text peuvent avoir plusieurs instances */
            for (ITrigger every : myKeeper) {
                if (trigger.getClass().equals(every.getClass())) {
                    /* refused */
                    return;
                }
            }
        }
        myKeeper.add(trigger);
    }

    /**
     * @return une vue (read-only) des triggers
     */
    @Override
    public Iterable<ITrigger> getTriggers() {
        return myKeeper;
    }

    /**
     * @param trigClassFull
     * @param prefix
     * @param args
     */
    public void loadTrigClass(String trigClassFull, String prefix, Collection<String> args) {
        Class<? extends ITrigger> trigClass = findClass(trigClassFull);
        if (trigClass != null) {
            if (args.isEmpty()) {
                LOG.debug("loading trigger: " + trigClass.getSimpleName());
                addTrigger(trigClass, (Object[]) null);
            } else {
                loadTrigClassWithArgs(trigClass, prefix, args);
            }
        }
    }

    private void loadTrigClassWithArgs(Class<? extends ITrigger> trigClass, String prefix, Collection<String> args) {
        for (String trigText : args) {
            if (prefix != null) {
                trigText = prefix + trigText;
            }
            LOG.debug("loading trigger with command '" + trigText + "': " + trigClass.getSimpleName());
            addTrigger(trigClass, trigText);
        }
    }

    @Override
    public MsgState processMsg(IIrcMessage msg, IIrcControl control) {
        if (msg == null) {
            LOG.warn("received null msg");
            return MsgState.INVALID;
        }
        LOG.debug("received " + msg.getClass().getSimpleName() + ": " + msg);
        new TriggerLoop(this, msg, control).launch();
        return MsgState.AVAILABLE;
    }
}
