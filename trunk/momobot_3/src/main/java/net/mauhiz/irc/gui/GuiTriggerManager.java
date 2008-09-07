package net.mauhiz.irc.gui;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.ITriggerManager;
import net.mauhiz.irc.base.IrcControl;
import net.mauhiz.irc.base.msg.IIrcMessage;

import org.eclipse.swt.widgets.Display;

/**
 * @author mauhiz
 */
public class GuiTriggerManager implements ITriggerManager {
    IIrcControl client = new IrcControl(this);
    BlockingQueue<IIrcMessage> incoming = new LinkedBlockingQueue<IIrcMessage>();
    
    /**
     * @return the client
     */
    public IIrcControl getClient() {
        return client;
    }
    
    /**
     * @return next msg
     */
    public IIrcMessage nextMsg() {
        return incoming.poll();
    }
    
    /**
     * @see net.mauhiz.irc.base.ITriggerManager#processMsg(net.mauhiz.irc.base.msg.IIrcMessage,
     *      net.mauhiz.irc.base.IrcControl)
     */
    @Override
    public void processMsg(final IIrcMessage msg, final IrcControl ircControl) {
        if (msg == null) {
            return;
        }
        try {
            incoming.put(msg);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
    
    /**
     * @see net.mauhiz.irc.base.ITriggerManager#shutdown()
     */
    @Override
    public void shutdown() {
        Display.getDefault().dispose();
    }
}
