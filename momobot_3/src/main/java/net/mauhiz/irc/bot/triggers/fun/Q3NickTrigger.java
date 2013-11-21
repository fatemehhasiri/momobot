package net.mauhiz.irc.bot.triggers.fun;

import net.mauhiz.irc.base.Color;
import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.IrcSpecialChars;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.UtfChar;

import org.apache.commons.lang3.text.StrBuilder;

/**
 * @author mauhiz
 */
public class Q3NickTrigger extends AbstractTextTrigger implements IPrivmsgTrigger, IrcSpecialChars {
    /**
     * TODO finir la methode.
     * 
     * @param colorCode
     * @return le code couleur q3
     */
    private static String computeQ3ColorCode(String colorCode) {
        String q3Code;
        if (Color.BLACK.toString().equals(colorCode)) {
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
    private static String createq3nick(String args) {
        StringBuilder q3nick = new StringBuilder();
        StrBuilder colorCode = new StrBuilder();
        int inColor = 0;
        for (int i = 0; i < args.length(); i++) {
            UtfChar c = UtfChar.charAt(args, i);
            if (c == DELIM_COLOR) {
                inColor = 1;
                continue;
            } else if (inColor == 1) {
                colorCode.clear();
                if (c.isDigit()) {
                    colorCode.append(c);
                    ++inColor;
                } else {
                    inColor = 0;
                }
            } else if (inColor == 2) {
                if (c.isDigit()) {
                    colorCode.append(c);
                }
                q3nick.append(computeQ3ColorCode(colorCode.toString()));
            } else {
                q3nick.append(c);
            }
        }
        return q3nick.toString();
    }

    /**
     * @param trigger
     */
    public Q3NickTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        Privmsg msg = new Privmsg(im, createq3nick(getTriggerContent(im)));
        control.sendMsg(msg);
    }
}
