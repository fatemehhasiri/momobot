package net.mauhiz.irc.bot.triggers.dispo;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.mauhiz.irc.base.Color;
import net.mauhiz.irc.base.ColorUtils;
import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.IPrivateIrcMessage;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.DateUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @author mauhiz
 */
public class YaquiTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * un espace.
     */
    private static final char SEP = ' ';

    /**
     * @param msg
     * @param heureDispo
     * @param heurePala
     * @return ?
     */
    private static StringBuilder appendDispos(StringBuilder msg, Collection<String> heureDispo,
            Collection<String> heurePala) {
        if (heureDispo.isEmpty()) {
            msg.append("personne ");
        } else {
            String heuresDispo = StringUtils.join(heureDispo, SEP);
            msg.append(ColorUtils.toColor(heuresDispo, Color.DARK_GREEN));
        }
        if (!heurePala.isEmpty()) {
            msg.append(" (absents : ");
            String heuresDispo = StringUtils.join(heurePala, SEP);
            msg.append(ColorUtils.toColor(heuresDispo, Color.RED));
            msg.append(") ");
        }
        return msg;
    }

    /**
     * @param trigger
     *            le trigger
     */
    public YaquiTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @param imsg
     * @param cal
     * @return msg
     */
    protected String doTrigger(IPrivateIrcMessage imsg, Calendar cal) {
        Date date = new java.sql.Date(cal.getTimeInMillis());
        Collection<String> heure1Dispo = new LinkedList<>();
        Collection<String> heure1Pala = new LinkedList<>();
        Collection<String> heure2Dispo = new LinkedList<>();
        Collection<String> heure2Pala = new LinkedList<>();
        List<Dispo> dispos = DispoDb.getInstance(imsg.getServerPeer().getNetwork()).getDispo(
                ((IrcChannel) imsg.getTo()).fullName(), date);
        for (Dispo dispo : dispos) {
            if (dispo.getPresent1() == Present.LA) {
                heure1Dispo.add(dispo.getQauth());
            } else if (dispo.getPresent1() == Present.PAS_LA) {
                heure1Pala.add(dispo.getQauth());
            }
            if (dispo.getPresent2() == Present.LA) {
                heure2Dispo.add(dispo.getQauth());
            } else if (dispo.getPresent2() == Present.PAS_LA) {
                heure2Pala.add(dispo.getQauth());
            }
        }
        StringBuilder msg = new StringBuilder(DateUtil.getJourFromDate(date, Locale.FRANCE));
        msg.append(" a 21h : ");
        appendDispos(msg, heure1Dispo, heure1Pala);
        msg.append(" et 22h30 : ");
        appendDispos(msg, heure2Dispo, heure2Pala);
        return msg.toString();
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        String msg = getTriggerContent(cme).toLowerCase(Locale.FRANCE);
        Calendar date;
        if ("semaine".equals(msg)) {
            date = Calendar.getInstance(Locale.FRANCE);
            for (int i = date.getMinimum(Calendar.DAY_OF_WEEK); i < date.getMaximum(Calendar.DAY_OF_WEEK); i++) {
                Privmsg resp = new Privmsg(cme, doTrigger(cme, date));
                control.sendMsg(resp);
                date.setTimeInMillis(date.getTimeInMillis() + DateUtils.MILLIS_PER_DAY);
            }
        } else if (StringUtils.isBlank(msg)) {
            /* dispo pour le jour meme */
            date = Calendar.getInstance(Locale.FRANCE);
            Privmsg resp = new Privmsg(cme, doTrigger(cme, date));
            control.sendMsg(resp);
        } else {
            /* dispo pour plus tard */
            try {
                date = DateUtil.getDateFromJour(msg, Locale.FRANCE);
                Privmsg resp = new Privmsg(cme, doTrigger(cme, date));
                control.sendMsg(resp);
            } catch (IllegalArgumentException iae) {
                Notice notice = new Notice(cme, getTriggerHelp(), true);
                control.sendMsg(notice);
            }
        }
    }

    /**
     * @see net.mauhiz.irc.bot.triggers.AbstractTextTrigger#getTriggerHelp()
     */
    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " jour[" + StringUtils.join(DateUtil.getWeekDays(Locale.FRANCE), '/')
                + "] ou $" + getTriggerText() + " semaine";
    }
}
