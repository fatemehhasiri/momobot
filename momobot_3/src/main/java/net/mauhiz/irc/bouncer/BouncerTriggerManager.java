package net.mauhiz.irc.bouncer;

import java.util.ArrayList;
import java.util.List;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.ITriggerManager;
import net.mauhiz.irc.base.msg.IIrcMessage;

/**
 * @author mauhiz
 */
public class BouncerTriggerManager implements ITriggerManager, Runnable {
    List<BncClientIO> currentlyConnected = new ArrayList<BncClientIO>();
    
    @Override
    public void processMsg(IIrcMessage msg, IIrcControl ircControl) {
        // FIXME this is nonsense. do something cool to handle :
        // multi-accounts
        // multi-clients (per account)
        // multi-server
        for (BncClientIO client : currentlyConnected) {
            client.sendMsg(msg.getIrcForm());
        }
    }
    
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (true) {
            /* FIXME receive client msgs */
            break;
        }
    }
    
    /**
     * @see net.mauhiz.irc.base.ITriggerManager#shutdown()
     */
    @Override
    public void shutdown() {
        // do nothing
    }
}
