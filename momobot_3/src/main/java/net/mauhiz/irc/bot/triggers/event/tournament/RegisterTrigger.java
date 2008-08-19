package net.mauhiz.irc.bot.triggers.event.tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.Channel;
import net.mauhiz.irc.base.data.IrcServer;
import net.mauhiz.irc.base.model.Channels;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.bot.event.ChannelEvent;
import net.mauhiz.irc.bot.tournament.Tournament;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.irc.bot.triggers.IPrivmsgTrigger;

/**
 * @author topper
 * 
 */
public class RegisterTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * @param string
     * @return String[] ou a decoupe idTeam et Country
     */
    private static List<String> cutList(final String[] string) {
        if (string.length < 3) {
            return null;
        }
        
        List<String> strReturn = new ArrayList<String>();
        for (int i = 3; i < string.length; i++) {
            strReturn.add(string[i]);
        }
        return strReturn;
    }
    
    /**
     * @param stg
     * @return boolean
     */
    private static Locale testLocale(final String stg) {
        for (Locale loc : Locale.getAvailableLocales()) {
            if (loc.getCountry().equalsIgnoreCase(stg)) {
                return loc;
            }
        }
        return null;
    }
    /**
     * @param trigger
     *            le trigger
     */
    public RegisterTrigger(final String trigger) {
        super(trigger);
    }
    
    /**
     * @see net.mauhiz.irc.bot.triggers.IPrivmsgTrigger#doTrigger(net.mauhiz.irc.base.msg.Privmsg,
     *      net.mauhiz.irc.base.IIrcControl)
     */
    @Override
    public void doTrigger(final Privmsg im, final IIrcControl control) {
        IrcServer server = im.getServer();
        Channel chan = Channels.getInstance(server).get(im.getTo());
        ChannelEvent event = chan.getEvt();
        if (event == null) {
            Privmsg msg = Privmsg.buildAnswer(im, "Aucun tournois n'est lance.");
            control.sendMsg(msg);
            return;
        }
        
        if (event instanceof Tournament) {
            String[] args = getArgs(im.getMessage()).split(" ");
            if (args.length > 3) {
                // on match l'id de la team
                if (Integer.parseInt(args[0]) > -1) {
                    int id = Integer.parseInt(args[0]);
                    // on match le pays
                    Locale loc = testLocale(args[1]);
                    if (loc != null) {
                        // on match le nom de la team mininum 3 caract�res
                        if (args[2].length() > 2) {
                            String tag = args[2];
                            // on d�coupe la liste
                            Privmsg msg = Privmsg.buildAnswer(im, ((Tournament) event).setTeam(id, loc, tag,
                                    cutList(args)));
                            control.sendMsg(msg);
                            return;
                        }
                        Privmsg msg = Privmsg.buildAnswer(im, "Erreur : nom de team doit etre > a 3 caracteres.");
                        control.sendMsg(msg);
                        return;
                    }
                    Privmsg msg = Privmsg.buildAnswer(im, "Erreur : Abr�viation du pays inconnu ex: FR");
                    control.sendMsg(msg);
                    return;
                }
                Privmsg msg = Privmsg.buildAnswer(im,
                        "Erreur : le premier argument doit etre un chiffre correspondant a l'ID de la team.");
                control.sendMsg(msg);
                return;
                
            }
            Privmsg msg = Privmsg
                    .buildAnswer(im,
                            "Erreur : parametre(s) insuffisant(s). ex : $tn-register 5 FR eule joueur1 joueur2 joueur3 joueur4 joueur5");
            control.sendMsg(msg);
            return;
        }
    }
}