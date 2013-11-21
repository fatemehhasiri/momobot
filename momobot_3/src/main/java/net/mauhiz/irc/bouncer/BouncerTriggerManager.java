package net.mauhiz.irc.bouncer;

import java.util.ArrayList;
import java.util.List;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.MsgState;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.trigger.DefaultTriggerManager;

/**
 * @author mauhiz
 */
public class BouncerTriggerManager extends DefaultTriggerManager implements Runnable {
    List<BncClientIO> currentlyConnected = new ArrayList<>();

    @Override
    public MsgState processMsg(IIrcMessage msg, IIrcControl ircControl) {
        // FIXME this is nonsense. do something cool to handle :
        // multi-accounts
        // multi-clients (per account)
        // multi-server
        for (BncClientIO client : currentlyConnected) {
            client.sendMsg(msg.getIrcForm());
        }

        return MsgState.NO_MORE_PROCESSING;
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
}
