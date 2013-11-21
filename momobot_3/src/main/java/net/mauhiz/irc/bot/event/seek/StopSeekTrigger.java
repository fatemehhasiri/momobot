package net.mauhiz.irc.bot.event.seek;

import java.util.Collection;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.msg.Part;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.Gather;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;

/**
 * @author mauhiz
 */
public class StopSeekTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * ON LEAVE LES CHANNELS DE SEEK
     * 
     * @param control
     * @param server
     */
    static void leaveSeekChans(IIrcControl control, IIrcServerPeer server) {
        Collection<String> channelSeek = SeekWar.SEEK_CHANS;
        for (String element : channelSeek) {
            Part leave = new Part(server, server.getNetwork().findChannel(element), null);
            control.sendMsg(leave);
        }
    }

    /**
     * @param trigger
     *            le trigger
     */
    public StopSeekTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        IrcChannel chan = (IrcChannel) im.getTo();
        IChannelEvent evt = chan.getEvt();
        String reply;
        if (evt == null) {
            reply = "Aucun gather n'est lance.";
        } else {
            if (evt instanceof Gather) {
                if (((Gather) evt).getSeek() == null) {
                    reply = "Le seek n'est pas lance.";
                } else {
                    // if (((Gather) evt).getSeek().isSeekInProgress()) {
                    reply = "Seek arrete.";
                    leaveSeekChans(control, im.getServerPeer());
                    ((Gather) evt).setSeekToNull();
                    // }
                }

            } else {
                return;
            }
        }
        Privmsg msg = new Privmsg(im, reply, false);
        control.sendMsg(msg);
    }
}
