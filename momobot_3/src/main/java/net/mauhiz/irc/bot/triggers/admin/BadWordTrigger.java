package net.mauhiz.irc.bot.triggers.admin;

import java.util.Set;
import java.util.TreeSet;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.data.UserChannelMode;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.msg.IPrivateIrcMessage;
import net.mauhiz.irc.base.msg.Kick;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

import org.apache.commons.lang3.text.StrTokenizer;

/**
 * @author mauhiz
 */
public class BadWordTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * TODO utiliser SQL.
     */
    private static final Set<String> BADWORDS = new TreeSet<>();
    static {
        BADWORDS.add("caca");
        BADWORDS.add("prout");
    }

    /**
     * @param trigger
     */
    public BadWordTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        if (im.getMessage().startsWith(getTriggerText())) {
            /* mode controle */
            /* TODO admin mode */
            String args = getTriggerContent(im);
            if (args.length() == 0) {
                showHelp(control, im);
            } else {
                BADWORDS.add(args);
                Privmsg resp = new Privmsg(im, "Badword ajoute: " + args, true);
                control.sendMsg(resp);
            }
        } else {
            for (String word : new StrTokenizer(im.getMessage()).getTokenArray()) {
                for (String badword : BADWORDS) {
                    if (badword.equalsIgnoreCase(word)) {
                        getAngry(im, control, badword);
                    }
                }
            }
        }
    }

    /**
     * @param toReply
     * @param control
     * @param badword
     */
    protected void getAngry(IPrivateIrcMessage toReply, IIrcControl control, String badword) {
        IIrcMessage resp;
        IIrcServerPeer server = toReply.getServerPeer();
        IrcChannel targetChan = (IrcChannel) toReply.getTo();

        if (targetChan != null && targetChan.getModes(server.getMyself()).contains(UserChannelMode.OP)) {
            /* hinhin, je suis op */
            resp = new Kick(server, null, targetChan, (IrcUser) toReply.getFrom(),
                    "You shall not use bad words such as " + badword);
        } else {
            /* je rage quand meme */
            resp = new Privmsg(toReply, toReply.getFrom() + " is so rude, he said " + badword + "!!");
        }
        control.sendMsg(resp);
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <badword>";
    }
}
