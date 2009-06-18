package net.mauhiz.irc.bot.triggers.admin;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IAdminTrigger;
import net.mauhiz.irc.bot.triggers.IPrivmsgTrigger;
import net.mauhiz.util.HibernateUtils;

/**
 * @author mauhiz
 */
public class ReloadDBTrigger extends AbstractTextTrigger implements IPrivmsgTrigger, IAdminTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public ReloadDBTrigger(String trigger) {
        super(trigger);
    }
    
    /**
     * @see net.mauhiz.irc.bot.triggers.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg pme, IIrcControl control) {
        HibernateUtils.closeSession();
        HibernateUtils.currentSession();
        String resp = "DB reloaded";
        Privmsg msg = Privmsg.buildPrivateAnswer(pme, resp);
        control.sendMsg(msg);
    }
}
