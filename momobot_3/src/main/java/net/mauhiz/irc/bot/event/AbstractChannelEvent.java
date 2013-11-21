package net.mauhiz.irc.bot.event;

import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.util.Hooks;

import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public abstract class AbstractChannelEvent implements IChannelEvent {
    /**
     * logger.
     */
    protected static final Logger LOG = Logger.getLogger(AbstractChannelEvent.class);

    /**
     * running
     */
    private boolean running = true;

    /**
     * @param chan
     *            le channel
     */
    public AbstractChannelEvent(IrcChannel chan) {
        Hooks.addHook(chan, this);
    }

    /**
     * @see net.mauhiz.irc.bot.event.IChannelEvent#isRunning()
     */
    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * @see net.mauhiz.irc.bot.event.IChannelEvent#stop()
     */
    @Override
    public String stop() {
        running = false;
        return "Fin du " + getClass().getSimpleName() + " !";
    }

    /**
     * Status
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public abstract String toString();
}
