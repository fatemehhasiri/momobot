package net.mauhiz.irc.bot.triggers.math;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.msg.IPrivateIrcMessage;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class BaseConvTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param toReply
     * @param control
     * @param base
     * @return si la base est acceptable
     */
    private static boolean checkBase(IPrivateIrcMessage toReply, IIrcControl control, int base) {
        if (base < Character.MIN_RADIX) {
            Notice not = new Notice(toReply, "La base " + base + " est trop petite", true);
            control.sendMsg(not);
            return false;
        } else if (base > Character.MAX_RADIX) {
            return false;
        }
        return true;
    }

    /**
     * @param trigger
     */
    public BaseConvTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        ArgumentList args = getArgs(cme);
        if (args.isEmpty()) {
            showHelp(control, cme);
            return;
        }
        String nombre = args.poll();
        int base1 = Integer.parseInt(args.poll());
        if (!checkBase(cme, control, base1)) {
            return;
        }
        IIrcMessage resp;
        try {
            int intNombre = Integer.parseInt(nombre, base1);
            int base2 = Integer.parseInt(args.poll());
            if (!checkBase(cme, control, base2)) {
                return;
            }
            String resultat = Integer.toString(intNombre, base2);
            resp = new Privmsg(cme, nombre + " en base " + base1 + " = " + resultat + " en base " + base2);
        } catch (NumberFormatException nfe) {
            resp = new Notice(cme, "Le nombre " + nombre + " est illisible en base " + base1, true);
        }
        control.sendMsg(resp);
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <number> <from-base> <to-base>";
    }
}
