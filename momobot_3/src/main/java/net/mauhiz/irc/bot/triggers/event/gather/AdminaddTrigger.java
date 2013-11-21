package net.mauhiz.irc.bot.triggers.event.gather;

import java.util.List;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.Gather;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.event.Pickup;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mauhiz
 */
public class AdminaddTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param input
     * @param with
     * @return replacement
     */
    public static String replaceTu(String input, String with) {
        return StringUtils.replaceEach(input, new String[] { "tu es ", "Tu es " }, new String[] { with + " est ",
                with + " est " });
    }

    /**
     * @param trigger
     *            le trigger
     */
    public AdminaddTrigger(String trigger) {
        super(trigger);
    }

    protected void addToPickup(Pickup pick, ArgumentList args, IIrcControl control, IrcChannel chan,
            IIrcServerPeer server) {
        List<String> whos = args.asList();
        String team = whos.get(whos.size() - 1);
        int max = whos.size();
        if (pick.assignTeam(team) != null) {
            --max;
        }
        for (int i = 0; i < max; i++) {
            IrcUser target = server.getNetwork().findUser(whos.get(i), false);
            if (target == null) {
                Privmsg msg = new Privmsg(server, null, chan, whos.get(i) + " n'est pas sur " + chan);
                control.sendMsg(msg);
                continue;
            }
            String resp = replaceTu(pick.add(target, team), target.getNick());
            Privmsg msg = new Privmsg(server, null, chan, resp);
            control.sendMsg(msg);
        }
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel chan = (IrcChannel) im.getTo();
        IChannelEvent event = chan.getEvt();
        if (event == null) {
            Privmsg msg = new Privmsg(im, "Aucun gather ou pickup n'est lance.");
            control.sendMsg(msg);
            return;
        }
        ArgumentList args = getArgs(im);
        if (args.isEmpty()) {
            return;
        }

        if (event instanceof Gather) {
            for (String who : args) {
                IrcUser target = im.getServerPeer().getNetwork().findUser(who, false);
                if (target == null) {
                    Privmsg msg = new Privmsg(im, who + " n'est pas sur " + chan);
                    control.sendMsg(msg);
                    continue;
                }
                String resp = replaceTu(((Gather) event).add(target), target.getNick());
                Privmsg msg = new Privmsg(im, resp);
                control.sendMsg(msg);
            }

        } else if (event instanceof Pickup) {
            addToPickup((Pickup) event, args, control, chan, im.getServerPeer());
        }
    }
}
