package net.mauhiz.irc.bot.triggers.admin;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IAdminTrigger;
import net.mauhiz.irc.bot.triggers.IPrivmsgTrigger;

/**
 * @author mauhiz
 */
public class SayTrigger extends AbstractTextTrigger implements IAdminTrigger, IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public SayTrigger(final String trigger) {
        super(trigger);
    }
    
    /**
     * @see net.mauhiz.irc.bot.triggers.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(final Privmsg pme, final IIrcControl control) {
        final String args = getArgs(pme.getMessage());
        final int index = args.indexOf(' ');
        if (index < 1) {
            Privmsg msg = Privmsg.buildAnswer(pme, "pas assez de param�tres.");
            control.sendMsg(msg);
            msg = Privmsg.buildAnswer(pme, "syntaxe $" + this + " target msg");
            control.sendMsg(msg);
        } else {
            /* TODO say cross-server ? */
            Privmsg msg = new Privmsg(null, args.substring(0, index), pme.getServer(), args.substring(index + 1));
            control.sendMsg(msg);
        }
    }
}
