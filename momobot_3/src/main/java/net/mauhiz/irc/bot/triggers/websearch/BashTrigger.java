package net.mauhiz.irc.bot.triggers.websearch;

import java.io.IOException;
import java.net.URISyntaxException;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.NetUtils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;

/**
 * @author mauhiz
 */
public class BashTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @return next bashfr quote
     */
    static String[] getNextFrQuote() {
        try {
            String page = NetUtils.doHttpGet("http://www.bashfr.org/?sort=random2");
            page = StringUtils.substringAfter(page, " class=\"com\"></span><br />");
            page = StringUtils.substringBefore(page, "</div>");
            page = StringUtils.replaceChars(page, "\r\n", "");
            String[] lignes = new StrTokenizer(page, "<br />").getTokenArray();
            for (int i = 0; i < lignes.length; i++) {
                lignes[i] = StringEscapeUtils.unescapeHtml4(lignes[i]);
            }
            return lignes;
        } catch (IOException e) {
            return new String[] { e.getLocalizedMessage() };

        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return next bash quote
     */
    static String[] getNextQuote() {
        try {
            String page = NetUtils.doHttpGet("http://www.bash.org/?random");
            page = StringUtils.substringAfter(page, "<p class=\"qt\">");
            page = StringUtils.substringBefore(page, "</p>");
            page = StringUtils.replaceChars(page, "\r\n", "");
            String[] lignes = new StrTokenizer(page, "<br />").getTokenArray();
            for (int i = 0; i < lignes.length; i++) {
                lignes[i] = StringEscapeUtils.unescapeHtml4(lignes[i]);
            }
            return lignes;
        } catch (IOException e) {
            return new String[] { e.getLocalizedMessage() };

        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param trigger
     */
    public BashTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        String args = getTriggerContent(im);
        String[] quote;
        if ("fr".equals(args)) {
            quote = getNextFrQuote();
        } else {
            quote = getNextQuote();
        }
        for (String quoteLine : quote) {
            Privmsg reply = new Privmsg(im, quoteLine);
            control.sendMsg(reply);
        }
    }
}
