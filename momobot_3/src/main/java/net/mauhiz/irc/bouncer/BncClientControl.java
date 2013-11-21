package net.mauhiz.irc.bouncer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.mauhiz.irc.base.AbstractIrcControl;
import net.mauhiz.irc.base.MsgState;
import net.mauhiz.irc.base.data.Target;
import net.mauhiz.irc.base.io.IIrcIO;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Privmsg;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class BncClientControl extends AbstractIrcControl {
    /**
     * logger.
     */
    private static final Logger LOG = Logger.getLogger(BncClientControl.class);

    private final Map<ClientPeer, BncClientIO> clientToIo = new HashMap<>();

    public BncClientControl() {
        super(new BouncerTriggerManager());
    }

    /**
     * @see net.mauhiz.irc.base.IIrcControl#close()
     */
    @Override
    public void close() {
        Collection<BncClientIO> ios = clientToIo.values();
        for (IIrcIO io : ios) {
            io.disconnect();
        }
    }

    @Override
    protected MsgState process(IIrcMessage message, IIrcIO io) {
        throw new NotImplementedException(); // TODO?
    }

    /**
     * @see net.mauhiz.irc.base.IIrcControl#sendMsg(net.mauhiz.irc.base.msg.IIrcMessage)
     */
    @Override
    public void sendMsg(IIrcMessage msg) {
        if (msg instanceof Privmsg) {
            if (StringUtils.isEmpty(((Privmsg) msg).getMessage())) {
                LOG.info("Not sending empty msg", new IllegalArgumentException());
            }
        } else if (msg instanceof Notice) {
            if (StringUtils.isEmpty(((Notice) msg).getMessage())) {
                LOG.info("Not sending empty msg", new IllegalArgumentException());
            }
        }
        LOG.info(msg);

        // FIXME this is wrong
        Target from = msg.getFrom();
        IIrcIO io = clientToIo.get(from);
        io.sendMsg(msg.getIrcForm());
    }
}
