package net.mauhiz.irc.bot.triggers.event.gather;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.Gather;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mauhiz
 */
public class GatherTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     */
    public GatherTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        IrcChannel chan = (IrcChannel) cme.getTo();
        IChannelEvent evt = chan.getEvt();
        String respMsg;
        if (evt == null) {
            IrcUser user = (IrcUser) cme.getFrom();
            Gather gather;
            String arg = getTriggerContent(cme);
            if (StringUtils.isNumeric(arg) && StringUtils.isNotBlank(arg)) {
                int nbPlayers = Integer.parseInt(arg);
                if (nbPlayers > 0) {
                    gather = new Gather(chan, nbPlayers);
                } else {
                    gather = new Gather(chan);
                }
            } else {
                gather = new Gather(chan);
            }
            gather.add(user);
            respMsg = "Gather lance par " + user.getNick();
        } else {
            respMsg = "Un " + evt.getClass().getSimpleName() + " est deja lance sur " + cme.getTo();
        }
        Privmsg resp = new Privmsg(cme, respMsg);
        control.sendMsg(resp);
    }
}
