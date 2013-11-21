package net.mauhiz.irc.bot.triggers.math;

import java.util.LinkedList;
import java.util.List;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * En voila un trigger qu'il est useless.
 * 
 * @author mauhiz
 */
public class FractionContinueTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    static final int LIMIT = 10;

    /**
     * TODO un critere d'arret base sur epsilon.
     * 
     * @param nombre
     * @return une fraction continue de type [a0; a1, a2, ...]
     */
    static String computeFractionContinue(double nombre) {
        List<Integer> fraction = new LinkedList<>();
        double work = nombre;

        for (int count = 0; count < LIMIT; ++count) {
            // LOG.debug("work = " + work);
            int floor = (int) Math.floor(work);
            if (floor == Integer.MAX_VALUE) {
                return displayFraction(fraction, true);
            }
            // LOG.debug("floor = " + floor);
            fraction.add(Integer.valueOf(floor));

            if (Double.compare(work, floor) == 0) {
                return displayFraction(fraction, true);
            }

            work = 1 / (work - floor);
            // LOG.debug("work = " + work);
        }

        return displayFraction(fraction, false);
    }

    /**
     * @param fraction
     * @param exactMatch
     * @return l'affichage de la fraction continue;
     */
    static String displayFraction(List<Integer> fraction, boolean exactMatch) {
        StringBuilder retour = new StringBuilder();
        retour.append("[");
        if (fraction.isEmpty()) {
            retour.append("0]");
        } else {
            retour.append(fraction.get(0));
            if (fraction.size() > 1) {
                retour.append("; ");
                for (Integer fracItem : fraction) {
                    retour.append(fracItem);
                    retour.append(", ");
                }
                if (exactMatch) {
                    retour.setLength(retour.length() - 2);
                } else {
                    retour.append("...");
                }

            }
            retour.append(']');
        }
        return retour.toString();
    }

    /**
     * @param trigger
     */
    public FractionContinueTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        String args = getTriggerContent(cme);
        Privmsg resp;

        try {
            double nombre = Double.parseDouble(args);
            String fraction = computeFractionContinue(nombre);
            resp = new Privmsg(cme, "fraction continue de " + nombre + ": " + fraction);
        } catch (NumberFormatException nfe) {
            resp = new Privmsg(cme, "l'argument doit etre un nombre (recu : " + args + ")");
        }

        control.sendMsg(resp);
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <number>";
    }
}
