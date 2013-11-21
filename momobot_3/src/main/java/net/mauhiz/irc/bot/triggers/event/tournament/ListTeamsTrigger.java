package net.mauhiz.irc.bot.triggers.event.tournament;

import java.util.List;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author topper
 * 
 */
public class ListTeamsTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public ListTeamsTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel chan = (IrcChannel) im.getTo();
        IChannelEvent event = chan.getEvt();
        if (event == null) {
            Privmsg msg = new Privmsg(im, "Aucun tournoi n'est lance.");
            control.sendMsg(msg);
            return;
        }

        if (event instanceof Tournament) {
            List<String> reply = ((Tournament) event).getListTeam();
            if (!reply.isEmpty()) {
                for (String element : reply) {
                    Notice msg = new Notice(im, element, true);
                    control.sendMsg(msg);
                }
            }
        }
    }
}
