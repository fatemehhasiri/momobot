package net.mauhiz.irc.bot.triggers.fun;

import java.util.Random;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class MargueriteTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {

    private static final String[] MARGUERITE = { "un peu", "beaucoup", "passionement", "a la folie", "pas du tout", };

    private static final Random RANDOM = new Random();

    /**
     * @param trigger
     */
    public MargueriteTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        String nom = getTriggerContent(cme);
        Privmsg msg = new Privmsg(cme, generateResponse(nom));
        control.sendMsg(msg);
    }

    /**
     * @param nom
     * @return un messsage
     */
    protected String generateResponse(String nom) {
        return nom + " t'aime " + MARGUERITE[RANDOM.nextInt(5)] + '.';
    }
}
