package net.mauhiz.irc.bot.triggers.math;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.FileUtil;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

/**
 * Calcule un sha-512.
 * 
 * @author mauhiz
 */
public class Sha512Trigger extends AbstractTextTrigger implements IPrivmsgTrigger {

    /**
     * @param input
     * @return le sha-512
     * @throws NoSuchAlgorithmException
     */
    static CharBuffer computeSha512(ByteBuffer input) throws NoSuchAlgorithmException {
        MessageDigest mdi = MessageDigest.getInstance("SHA-512");
        Hex hex = new Hex(FileUtil.ASCII.name());
        return FileUtil.ASCII.decode(ByteBuffer.wrap(hex.encode(mdi.digest(input.array()))));
    }

    /**
     * @param trigger
     */
    public Sha512Trigger(String trigger) {
        super(trigger);
    }

    /**
     * MessageDigest
     */
    @Override
    public void doTrigger(Privmsg cme, IIrcControl control) {
        String args = getTriggerContent(cme);

        if (StringUtils.isEmpty(args)) {
            showHelp(control, cme);
        } else {
            Privmsg resp;
            try {
                resp = new Privmsg(cme, "sha-512 de " + args + ": " + computeSha512(FileUtil.ISO8859_15.encode(args)));
            } catch (NoSuchAlgorithmException nsae) {
                resp = new Privmsg(cme, "J'ai pas de sha-512. Sry.");
            }
            control.sendMsg(resp);
        }
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <data>";
    }
}
