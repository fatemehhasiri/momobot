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

/**
 * @author mauhiz
 */
public class VdmTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {

    /**
     * @return next VDM
     */
    static String getNextVdm() {
        try {
            String page = NetUtils.doHttpGet("http://www.viedemerde.fr/aleatoire");
            page = StringUtils.substringAfter(page, "<div class=\"post\"><p>");
            page = StringUtils.substringBefore(page, "</p>");
            page = StringUtils.replaceChars(page, "\r\n", "");
            return StringEscapeUtils.unescapeHtml4(page);

        } catch (IOException e) {
            return e.getMessage();

        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param trigger
     */
    public VdmTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        String vdm = getNextVdm();
        int maxLen = im.getServerPeer().getNetwork().getLineMaxLength() - 50; // TODO make precise computation of overhead in PRIVMSG
        while (vdm.length() > maxLen) {
            int spc = StringUtils.indexOf(vdm, ' ', maxLen);
            Privmsg reply = new Privmsg(im, vdm.substring(0, spc));
            control.sendMsg(reply);
            vdm = vdm.substring(spc + 1);
        }
        if (vdm.length() != 0) {
            Privmsg reply = new Privmsg(im, vdm);
            control.sendMsg(reply);
        }
    }
}
