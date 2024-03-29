package net.mauhiz.irc.bot.triggers.spam;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.Target;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.util.AbstractDaemon;

/**
 * @author mauhiz
 */
public class SpamRunnable extends AbstractDaemon {
    /**
     * control.
     */
    private final IIrcControl control;
    /**
     * 
     */
    private final long delayMs;

    /**
     * 
     */
    private final Privmsg spamMsg;

    /**
     * @param spamMsg1
     * @param control1
     * @param delayMs1
     */
    public SpamRunnable(Privmsg spamMsg1, IIrcControl control1, long delayMs1) {
        super("Spam");
        control = control1;
        delayMs = delayMs1;
        spamMsg = spamMsg1;
    }

    /**
     * @return the delayMs
     */
    public long getDelayMs() {
        return delayMs;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return spamMsg.getMessage();
    }

    /**
     * @return the targetChan
     */
    public Target getTarget() {
        return spamMsg.getTo();
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void trun() {
        while (isRunning()) {
            pause(delayMs);
            control.sendMsg(spamMsg);
        }
    }
}
