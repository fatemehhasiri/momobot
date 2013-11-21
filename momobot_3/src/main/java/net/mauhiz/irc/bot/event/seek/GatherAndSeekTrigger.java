package net.mauhiz.irc.bot.event.seek;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.data.Target;
import net.mauhiz.irc.base.data.fake.FakeUser;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author Topper
 */
public class GatherAndSeekTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    private static String getResp(ArgumentList args, Target target, IrcChannel chan) {
        try {
            int nbPlayers = Integer.parseInt(args.poll());
            if (nbPlayers > 0 && nbPlayers < 5) {
                IrcUser user = (IrcUser) target;
                GatherAndSeek gath = new GatherAndSeek(chan, nbPlayers);
                for (int i = 0; i < nbPlayers; i++) {
                    IrcUser ircuser = new FakeUser("P" + (i + 1));
                    gath.add(ircuser);
                }
                return "Gather lance par " + user.getNick();
            }
        } catch (NumberFormatException e) {
            // invalid arg
        }
        return "Erreur : L'argument qui suit doit etre un chiffre entre 1 et 5";
    }

    /**
     * @param trigger
     */
    public GatherAndSeekTrigger(String trigger) {
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
            ArgumentList args = getArgs(cme);
            respMsg = getResp(args, cme.getFrom(), chan);

        } else {
            respMsg = "Un " + evt.getClass().getSimpleName() + " est deja lance sur " + cme.getTo();
        }

        Privmsg resp = new Privmsg(cme, respMsg, false);
        control.sendMsg(resp);
    }
}
