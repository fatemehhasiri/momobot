package net.mauhiz.irc.bot.triggers.dispo;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.data.WhoisRequest;
import net.mauhiz.irc.base.data.qnet.QnetUser;
import net.mauhiz.irc.base.msg.IPrivateIrcMessage;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.DateUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mauhiz
 */
public class DispoTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    private static final String[] DISPOS = { "oui", "non", "?" };
    private static final String[] HEURES = { "21h", "22h30" };

    private static void whoisUser(QnetUser quser, IPrivateIrcMessage cme, IIrcControl control) {
        if (StringUtils.isEmpty(quser.getAuth())) {
            WhoisRequest.startWhois(cme.getServerPeer(), control, Collections.singleton(quser.getNick()), null);
            WhoisRequest whois = WhoisRequest.get(quser.getNick());

            /* on attend le whois */
            while (whois.isRunning()) {
                Thread.yield();
            }

            if (StringUtils.isEmpty(quser.getAuth())) {
                Notice notice = new Notice(cme, "il faut etre auth sur Qnet pour utiliser cette fonction", true);
                control.sendMsg(notice);
                return;
            }
        }
    }

    /**
     * @param trigger
     *            le trigger
     */
    public DispoTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        IrcUser user = (IrcUser) cme.getFrom();

        if (!(user instanceof QnetUser)) {
            LOG.error("user non Qnet: " + user);
            return;
        }

        QnetUser quser = (QnetUser) user;
        whoisUser(quser, cme, control);

        ArgumentList args = getArgs(cme);

        if (args.isEmpty()) {
            showHelp(control, cme);

        } else {
            Calendar date;
            try {
                date = DateUtil.getDateFromJour(args.poll(), Locale.FRANCE); /* tokens finis */

            } catch (IllegalArgumentException iae) {
                showHelp(control, cme);
                return;
            }

            Dispo dispo = new Dispo();
            dispo.setChannel(((IrcChannel) cme.getTo()).fullName());
            Present[] heures = new Present[HEURES.length];
            List<String> ouinons = args.asList();
            for (int i = 0; i < HEURES.length && i < ouinons.size(); i++) {
                String nextArg = ouinons.get(i);
                if (nextArg.equalsIgnoreCase("oui")) {
                    heures[i] = Present.LA;
                } else if (nextArg.equalsIgnoreCase("non")) {
                    heures[i] = Present.PAS_LA;
                }
            }
            dispo.setPresent1(heures[0]);
            dispo.setPresent2(heures[1]);
            dispo.setQauth(quser.getAuth());
            dispo.setServerAlias(cme.getServerPeer().getNetwork().getAlias());
            dispo.setQuand(new java.sql.Date(date.getTimeInMillis()));
            DispoDb.updateDispo(dispo);
            Notice notice = new Notice(cme, "dispo enregistree pour le " + DateUtil.DATE_FORMAT.format(date), true);
            control.sendMsg(notice);

        }
    }

    @Override
    public String getTriggerHelp() {
        StringBuilder msg = new StringBuilder();
        msg.append(super.getTriggerHelp());
        msg.append(" jour[");
        msg.append(StringUtils.join(DateUtil.getWeekDays(Locale.FRANCE), '/'));
        msg.append("] ");
        for (String heure : HEURES) {
            msg.append(heure).append("[").append(StringUtils.join(DISPOS, '/')).append("] ");
        }
        return msg.toString();
    }
}
