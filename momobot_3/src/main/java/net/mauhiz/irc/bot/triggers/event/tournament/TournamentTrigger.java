package net.mauhiz.irc.bot.triggers.event.tournament;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author Topper
 * 
 */
public class TournamentTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     */
    public TournamentTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        IrcChannel chan = (IrcChannel) cme.getTo();
        IChannelEvent evt = chan.getEvt();
        String respMsg;
        if (evt == null) {
            // On cree le tn en fonction des parametres
            // On pecho les params
            ArgumentList params = getArgs(cme);

            // Check si les params st ok
            if (params.isEmpty()) {
                showHelp(control, cme);
                return;
            }

            Tournament tn = new Tournament(chan, params.asList());
            tn.generateTemplate();
            respMsg = tn.toString();

        } else {
            respMsg = "Un " + evt.getClass().getSimpleName() + " est deja lance sur " + cme.getTo();
        }
        Privmsg resp = new Privmsg(cme, respMsg);
        control.sendMsg(resp);
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " [<map>]+";
    }
}
