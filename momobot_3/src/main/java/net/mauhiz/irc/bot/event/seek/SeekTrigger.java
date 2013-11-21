package net.mauhiz.irc.bot.event.seek;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Join;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.event.Gather;
import net.mauhiz.irc.bot.event.IChannelEvent;
import net.mauhiz.irc.bot.triggers.AbstractGourmandTrigger;

/**
 * MEMO: de chose a revoir:<br>
 * 1) charger les valeurs avec le xml<br>
 * 2) detecter une adresse IP + Port pour un msg PV<br>
 * 3) sur le lister tout les gathers en cour et executer l'action correspondant
 * 
 * @author Topper
 */
public class SeekTrigger extends AbstractGourmandTrigger implements IPrivmsgTrigger {
    /**
     * @param trigger
     *            le trigger
     */
    public SeekTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        if (isCommandMsg(im.getMessage())) {
            IrcChannel chan = (IrcChannel) im.getTo();
            if (chan == null) {
                /* msg prive */
                return;
            }
            IChannelEvent evt = chan.getEvt();
            if (evt == null) {
                Privmsg resp = new Privmsg(im, "Aucun gather n'est lance.", false);
                control.sendMsg(resp);
            } else if (evt instanceof Gather) {
                String reply;
                Gather gather = (Gather) evt;
                SeekWar seek = gather.getSeek();
                if (seek == null) {
                    seek = gather.createSeekWar();
                    reply = seek.start(getArgs(im), chan, gather.getNumberPlayers());
                    if (seek.isSeekInProgress()) {
                        // L'initialisation a reussi
                        Collection<String> channelSeek = SeekWar.SEEK_CHANS;

                        for (String element : channelSeek) {
                            Join go = new Join(im.getServerPeer(), im.getServerPeer().getNetwork().findChannel(element));
                            control.sendMsg(go);
                        }

                        // ON ENVOIE LES MSG DE SEEK

                        for (String element : channelSeek) {
                            Privmsg resp = new Privmsg(im.getServerPeer(), null, im.getServerPeer().getNetwork()
                                    .findChannel(element), seek.getMessageForSeeking());
                            control.sendMsg(resp);
                        }

                    } else {
                        // Le seek a foire! on notice le noob pour lui donner la syntaxe
                        gather.setSeekToNull();
                        showHelp(control, im);
                    }
                } else if (seek.isSeekInProgress()) {
                    reply = "Seek deja en cours.";
                } else {
                    reply = "";
                }

                Privmsg resp = new Privmsg(im, reply, false);
                control.sendMsg(resp);

            }
        } else {
            IrcUser myself = im.getServerPeer().getMyself();
            Set<IrcChannel> myChans = im.getServerPeer().getNetwork().getChannelsForUser(myself);
            for (IrcChannel chan1 : myChans) {
                IChannelEvent evt1 = chan1.getEvt();
                // if (evt instanceof Gather) {
                if (evt1 instanceof Gather) {
                    Gather gather = (Gather) evt1;
                    IrcUser kikoolol = (IrcUser) im.getFrom();
                    SeekWar seek = gather.getSeek();
                    if (seek != null) {
                        // if (seek.isSeekInProgress()) {
                        if (seek.isTimeOut()) {
                            // !! Time out !!
                            LOG.debug("Seek-Time-Out atteint.");
                            gather.setSeekToNull();
                            Privmsg resp = new Privmsg(im.getServerPeer(), null, seek.getChannel(),
                                    "Time out atteint, seek stopper.");
                            control.sendMsg(resp);
                            // LEAVE LES CHANNELS
                            StopSeekTrigger.leaveSeekChans(control, im.getServerPeer());
                        } else {
                            // TJS en vie
                            LOG.debug("MSG entrant : " + im);
                            // Si c'est le winner du seek, je transmet le msg PV
                            if (seek.getSeekWinner().equals(kikoolol.getNick())
                                    && im.getTo().equals(im.getServerPeer().getMyself())) {
                                Privmsg reply = new Privmsg(im.getServerPeer(), null, seek.getChannel(),
                                        kikoolol.getNick() + " : " + im.getMessage());
                                control.sendMsg(reply);
                            } else if (seek.isSeekInProgress()) {
                                List<Privmsg> replies = seek.submitSeekMessage(im);
                                for (Privmsg element1 : replies) {
                                    control.sendMsg(element1);
                                }
                            } else if (!seek.isSeekInProgress() && seek.isLaunchedAndQuit) {
                                seek.isLaunchedAndQuit = false;
                                StopSeekTrigger.leaveSeekChans(control, im.getServerPeer());
                            }

                        }
                    }
                }
            }
        }
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " (<on|off>) (<ip-pass>) (<level>) (<seek-message>)";
    }
}
