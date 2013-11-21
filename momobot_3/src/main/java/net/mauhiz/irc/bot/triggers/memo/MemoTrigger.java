package net.mauhiz.irc.bot.triggers.memo;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class MemoTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public MemoTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        ArgumentList msg = getArgs(cme);
        String respMsg;
        if (msg.isEmpty()) {
            respMsg = "Je connais " + MemoDb.getInstance(cme.getServerPeer().getNetwork()).countMemos()
                    + " memos. $listmemos pour avoir une liste";
        } else {
            String cle = msg.poll();
            if (msg.isEmpty()) {
                respMsg = MemoDb.getInstance(cme.getServerPeer().getNetwork()).getMemo(cle);
            } else {
                respMsg = MemoDb.getInstance(cme.getServerPeer().getNetwork()).setMemo(cle, msg.getRemainingData());
            }
        }
        Privmsg resp = new Privmsg(cme, respMsg);
        control.sendMsg(resp);
    }
}
