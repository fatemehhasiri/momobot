package net.mauhiz.irc.bot.event;

import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.util.Hooks;

import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public abstract class ChannelEvent implements IChannelEvent {
    /**
     * logger.
     */
    protected static final Logger LOG = Logger.getLogger(Gather.class);
    
    /**
     * running
     */
    private boolean running = true;
    
    /**
     * @param chan
     *            le channel
     */
    public ChannelEvent(IrcChannel chan) {
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
