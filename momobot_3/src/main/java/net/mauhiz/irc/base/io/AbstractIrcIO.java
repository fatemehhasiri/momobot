package net.mauhiz.irc.base.io;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IrcPeer;
import net.mauhiz.irc.base.io.output.IIrcOutput;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public abstract class AbstractIrcIO implements IIrcIO {
    private static final Logger LOG = Logger.getLogger(AbstractIrcIO.class);
    protected final IIrcControl control;
    protected IIrcOutput output;
    protected final IrcPeer peer;

    protected IOStatus status;

    protected AbstractIrcIO(IIrcControl control, IrcPeer peer) {
        super();
        this.control = control;
        this.peer = peer;
    }

    /**
     * @see net.mauhiz.irc.base.io.IIrcIO#getStatus()
     */
    @Override
    public IOStatus getStatus() {
        return status;
    }

    /**
     * @see net.mauhiz.irc.base.io.IIrcIO#processMsg(java.lang.String)
     */
    @Override
    public void processMsg(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        try {
            control.decodeIrcRawMsg(msg, this);
        } catch (RuntimeException rex) {
            LOG.error("Error while processing msg: " + msg, rex); // dont let it crash
        }
    }

    /**
     * @see net.mauhiz.irc.base.io.IIrcIO#sendMsg(String)
     */
    @Override
    public final void sendMsg(String msg) {
        int maxLen = peer.getNetwork().getLineMaxLength();
        String trimmedMsg = msg.length() > maxLen ? msg.substring(0, maxLen) : msg;

        output.sendRawMsg(trimmedMsg);
    }

    /**
     * @see net.mauhiz.irc.base.io.IIrcIO#setStatus(IOStatus)
     */
    @Override
    public void setStatus(IOStatus status) {
        this.status = status;
    }
}
