package net.mauhiz.irc.bot.triggers.cs;

import java.io.IOException;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.NetUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mauhiz
 */
public class TopFraggerTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    private static Player findBestPlayer(IServer server) {
        Player best = null;
        int bestScore = Integer.MIN_VALUE;
        for (Player player : server.getPlayers()) {
            if (player == null) {
                continue;
            } else if (player.getFrags() > bestScore) {
                best = player;
                bestScore = player.getFrags();
            }
        }
        return best;
    }

    /**
     * @param trigger
     *            le trigger
     */
    public TopFraggerTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        String args = getTriggerContent(im);
        if (StringUtils.isBlank(args)) {
            Privmsg help = new Privmsg(im, getTriggerHelp(), true);
            control.sendMsg(help);
            return;
        }

        IServer server = new Server(NetUtils.makeISA(args));
        try {
            server.getDetails();
        } catch (IOException ioe) {
            LOG.warn(ioe, ioe);
            Privmsg help = new Privmsg(im, "Could not connect to server: " + ioe.getLocalizedMessage(), true);
            control.sendMsg(help);
            return;
        }
        Player best = findBestPlayer(server);

        String reply;
        if (best == null) {
            reply = "There is no player on this server";

        } else {
            reply = "Best fragger on " + server.getName() + " : " + best.getName() + " with " + best.getFrags()
                    + " frags";
        }

        Privmsg msg = new Privmsg(im, reply);
        control.sendMsg(msg);
    }
}
