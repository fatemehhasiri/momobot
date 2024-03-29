package momobot.websearch;

import ircbot.Channel;
import ircbot.IrcUser;
import ircbot.trigger.AbstractTrigger;
import ircbot.trigger.IPublicTrigger;

import java.io.IOException;
import java.net.URLEncoder;

import momobot.MomoBot;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class BabelfishTrigger extends AbstractTrigger implements IPublicTrigger {
    /**
     * encodage.
     */
    private static final String     ENCODE = "utf-8";
    /**
     * logger.
     */
    private static final Logger     LOG    = Logger.getLogger(BabelfishTrigger.class);
    /**
     * ma m�thode de post.
     */
    private static final PostMethod POST   = new PostMethod("http://babelfish.altavista.com/tr");

    /**
     * @param langue1
     *            la langue de d�part
     * @param langue2
     *            la langue de destination
     * @param toTranslate
     *            le message � traduire
     * @return le message traduit
     * @throws IOException
     *             si le site est en mousse
     */
    public static String result(final String langue1, final String langue2, final String toTranslate)
            throws IOException {
        final NameValuePair[] data = {
        new NameValuePair("trtext", URLEncoder.encode(toTranslate, ENCODE)),
        new NameValuePair("lp", URLEncoder.encode(langue1 + '_' + langue2, ENCODE)),
        new NameValuePair("tt", "urltext"),
        new NameValuePair("intl", "tt"),
        new NameValuePair("doit", "done"), };
        POST.setRequestBody(data);
        new HttpClient().executeMethod(POST);
        String page = POST.getResponseBodyAsString();
        final String bound1 = "<td bgcolor=white class=s><div style=padding:10px;>";
        final int len = bound1.length();
        int index = page.indexOf(bound1) + len;
        if (index <= len) {
            throw new IOException("�chec ://");
        }
        page = page.substring(index);
        final String bound2 = "</div></td>";
        index = page.indexOf(bound2);
        return page.substring(0, index);
    }

    /**
     * @param trigger
     *            le trigger
     */
    public BabelfishTrigger(final String trigger) {
        super(trigger);
    }

    /**
     * @see ircbot.trigger.IPublicTrigger#executePublicTrigger(ircbot.IrcUser, ircbot.Channel, java.lang.String)
     */
    @SuppressWarnings("unused")
    public final void executePublicTrigger(final IrcUser user, final Channel channel, final String message) {
        try {
            String msg = getArgs(message);
            final String lang1 = StringUtils.substringBefore(msg, " ");
            msg = StringUtils.substringAfter(msg, " ");
            final String lang2 = StringUtils.substringBefore(msg, " ");
            msg = StringUtils.substringAfter(msg, " ");
            MomoBot.getBotInstance().sendNotice(user, result(lang1, lang2, msg));
        } catch (final IOException ioe) {
            LOG.warn(ioe, ioe);
            MomoBot.getBotInstance().sendNotice(user,
                    "syntaxe : $" + getTriggerText() + " lang1[fr/en/..] lang2[fr/en/...] texte");
        }
    }
}
