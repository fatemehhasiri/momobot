package net.mauhiz.irc.bot.triggers.spam;

import java.io.IOException;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author mauhiz
 */
public class UrlHitterTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     */
    public UrlHitterTrigger(String trigger) {
        super(trigger);
    }

    /**
     * TODO cron pour le url hitter ?
     * 
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        String urlStr = getTriggerContent(im);
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(urlStr);
        String resp;
        try {
            client.execute(get);
            resp = "done";
        } catch (IOException e) {
            resp = "erreur : " + e.getLocalizedMessage();
        }
        Privmsg reply = new Privmsg(im, resp);
        control.sendMsg(reply);
    }
}
