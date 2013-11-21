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
 * Calcule un sha-256.
 * 
 * @author mauhiz
 */
public class Sha256Trigger extends AbstractTextTrigger implements IPrivmsgTrigger {

    /**
     * @param input
     * @return le sha-256
     * @throws NoSuchAlgorithmException
     */
    static CharBuffer computeSha256(ByteBuffer input) throws NoSuchAlgorithmException {
        MessageDigest mdi = MessageDigest.getInstance("SHA-256");
        Hex hex = new Hex(FileUtil.ASCII.name());
        return FileUtil.ASCII.decode(ByteBuffer.wrap(hex.encode(mdi.digest(input.array()))));
    }

    private static String getResp(String args) {
        try {
            return "sha-256 de " + args + ": " + computeSha256(FileUtil.ISO8859_15.encode(args));
        } catch (NoSuchAlgorithmException nsae) {
            return "J'ai pas de sha-256. Sry.";
        }
    }

    /**
     * @param trigger
     */
    public Sha256Trigger(String trigger) {
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
            String respMsg = getResp(args);
            Privmsg resp = new Privmsg(cme, respMsg);
            control.sendMsg(resp);
        }
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp() + " <data>";
    }
}
