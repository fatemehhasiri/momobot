package net.mauhiz.irc.bot.triggers.event.tournament;

import java.util.List;

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
public class ResultTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public ResultTrigger(String trigger) {
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
            Privmsg msg = new Privmsg(im, "Aucun tournois n'est lance.");
            control.sendMsg(msg);
            return;
        }
        if (event instanceof Tournament) {
            ArgumentList args = getArgs(im);

            try {
                int id = Integer.parseInt(args.poll());
                int score1 = Integer.parseInt(args.poll());
                int score2 = Integer.parseInt(args.poll());
                if (id > -1 && score1 > -1 && score2 > -1) {
                    Tournament tn = (Tournament) event;
                    List<String> str = tn.setScore(id, score1, score2);
                    for (String element : str) {
                        Privmsg msg = new Privmsg(im, element);
                        control.sendMsg(msg);
                    }
                    tn.generateTemplate();
                    return;
                }
            } catch (NumberFormatException nfe) {
                // handled later
            }

            showHelp(control, im);
        }

    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <team-id> <score-home> <score-visitor>";
    }
}
