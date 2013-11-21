package net.mauhiz.irc.bot.triggers.event.tournament;

import java.util.Locale;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author topper
 * 
 */
public class RegisterTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {

    /**
     * @param stg
     * @return boolean
     */
    private static Locale getLocale(String stg) {
        for (Locale loc : Locale.getAvailableLocales()) {
            if (loc.getCountry().equalsIgnoreCase(stg)) {
                return loc;
            }
        }
        return null;
    }

    /**
     * @param trigger
     *            le trigger
     */
    public RegisterTrigger(String trigger) {
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
            Tournament tn = (Tournament) event;
            if (tn.isReady()) {
                ArgumentList args = getArgs(im);
                String locStr = args.poll();
                String tag = args.poll();
                if (tag == null || args.isEmpty()) {
                    showHelp(control, im);
                    return;
                }

                // le pays
                Locale loc = getLocale(locStr);

                if (loc == null) {
                    Privmsg msg = new Privmsg(im, "Erreur : Abbreviation du pays inconnu ex: FR");
                    control.sendMsg(msg);
                    return;
                }

                // nom de la team mininum 3 caracteres

                if (tag.length() <= 3) {
                    Privmsg msg = new Privmsg(im, "Erreur : nom de team doit etre > a 3 caracteres.");
                    control.sendMsg(msg);
                    return;
                }

                // on decoupe la liste
                IrcUser ircuser = (IrcUser) im.getFrom();
                Privmsg msg = new Privmsg(im, tn.setTeam(ircuser, loc, tag, args.asList()), true);

                // on lance un status
                Privmsg msg1 = new Privmsg(im, tn.getStatus());
                control.sendMsg(msg);
                control.sendMsg(msg1);

                return;

            }
            Privmsg msg = new Privmsg(im, "Erreur : Tournoi deja complet.");
            control.sendMsg(msg);
            return;

        }
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <pays{FR|EN|..}> <tag> [<player>]+";
    }
}
