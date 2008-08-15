package net.mauhiz.irc.bot.triggers.fun;

import net.mauhiz.irc.base.Color;
import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.IrcSpecialChars;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IPrivmsgTrigger;

import org.apache.commons.lang.text.StrBuilder;

/**
 * @author mauhiz
 */
public class Q3NickTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * TODO finir la methode.
     * 
     * @param colorCode
     * @return le code couleur q3
     */
    private static String computeQ3ColorCode(final String colorCode) {
        final String q3Code;
        if (Color.BLACK.getCode().equals(colorCode)) {
            q3Code = "0";
        } else {
            return "";
        }
        return "^" + q3Code;
    }
    
    /**
     * @param args
     * @return un nick q3
     */
    private static String createq3nick(final String args) {
        final StrBuilder q3nick = new StrBuilder();
        String colorCode = "";
        int inColor = 0;
        for (int i = 0; i < args.length(); ++i) {
            if (args.charAt(i) == IrcSpecialChars.COLOR) {
                inColor = 1;
                continue;
            } else if (inColor == 1) {
                if (Character.isDigit(args.charAt(i))) {
                    colorCode = "" + args.charAt(i);
                    ++inColor;
                } else {
                    inColor = 0;
                    colorCode = "";
                }
            } else if (inColor == 2) {
                if (Character.isDigit(args.charAt(i))) {
                    colorCode = colorCode + args.charAt(i);
                }
                q3nick.append(computeQ3ColorCode(colorCode));
            } else {
                q3nick.append(args.charAt(i));
            }
        }
        return q3nick.toString();
    }
    
    /**
     * @param trigger
     */
    public Q3NickTrigger(final String trigger) {
        super(trigger);
    }
    
    /**
     * @see net.mauhiz.irc.bot.triggers.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(final Privmsg im, final IIrcControl control) {
        Privmsg msg = Privmsg.buildAnswer(im, createq3nick(getArgs(im.getMessage())));
        control.sendMsg(msg);
    }
}