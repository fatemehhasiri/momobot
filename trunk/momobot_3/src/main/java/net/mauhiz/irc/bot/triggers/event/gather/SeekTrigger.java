package net.mauhiz.irc.bot.triggers.event.gather;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.Channel;
import net.mauhiz.irc.base.model.Channels;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.bot.event.ChannelEvent;
import net.mauhiz.irc.bot.event.Gather;
import net.mauhiz.irc.bot.event.SeekWar;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IPrivmsgTrigger;

/**
 * @author Topper
 */

public class SeekTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    
    /**
     * @param trigger
     *            le trigger
     */
    
    public SeekTrigger(final String trigger) {
        super(trigger);
    }
    
    /**
     * @see net.mauhiz.irc.bot.triggers.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(final Privmsg im, final IIrcControl control) {
        Channel chan = Channels.get(im.getServer()).getChannel(im.getTo());
        ChannelEvent evt = ChannelEvent.CHANNEL_EVENTS.get(chan);
        String reply = "";
        if (isCommandMsg(im.getMessage())) {
            
            if (evt == null) { // ou != a test�
                reply = "Aucun gather n'est lance.";
            } else {
                if (evt instanceof Gather) {
                    if (((SeekWar) evt).isSeekInProgress()) {
                        reply = "Seek d�ja en cour.";
                    } else {
                        reply = new SeekWar(chan, im.getMessage().split(" ")).toString();
                        
                    }
                }
            }
            
            // Privmsg resp = Privmsg.buildAnswer(im, reply);
            // control.sendMsg(resp);
        }
        if (evt instanceof Gather) {
            final SeekWar seekwar = (SeekWar) evt;
            if (seekwar.isSeekInProgress()) {
                String tmp = im.getTo();
                boolean msgChannel = true;
                if (im.getTo().isEmpty() || im.getTo() == "MMB v3") {
                    tmp = im.getFrom();
                    msgChannel = false;
                }
                reply = seekwar.submitSeekMessage(im.getMessage(), msgChannel, im.getTo());
                
            }
            
            if (im.getMessage().length() == 1) {
                // reply = gather.submitLettre(im.getMessage().toLowerCase(Locale.FRANCE).charAt(0)).toString();
            } else {
                // reply = pendu.submitMot(im.getMessage()).toString();
            }
            // Privmsg resp = Privmsg.buildAnswer(im, respMsg);
            // control.sendMsg(resp);
        }
        if (reply.isEmpty()) {
            Privmsg resp = Privmsg.buildAnswer(im, reply);
            control.sendMsg(resp);
        }
        
        /**
         * String reply; if (evt == null) { reply = "Aucun gather n'est lance."; } else { if (evt instanceof Gather) {
         * if (((Gather) evt).isSeekInProgress()) { reply = "Seek d�ja en cour.";
         * 
         * } else { reply = ((Gather) evt).seek(im.getMessage().split(" ")); }
         * 
         * } else { return; } } Privmsg msg = Privmsg.buildAnswer(im, reply); control.sendMsg(msg);
         **/
    }
    
    /**
     * @see net.mauhiz.irc.bot.triggers.AbstractTextTrigger#isActivatedBy(java.lang.String)
     */
    @Override
    public boolean isActivatedBy(final String msg) {
        return true;
    }
    
    /**
     * @param msg
     * @return si il s'agit bien du trigger.
     */
    public boolean isCommandMsg(final String msg) {
        return super.isActivatedBy(msg);
    }
    
}
