package net.mauhiz.irc.base;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcDecoder;
import net.mauhiz.irc.base.io.IIrcIO;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.trigger.ITriggerManager;

/**
 * @author mauhiz
 */
public abstract class AbstractIrcControl implements IIrcControl {
    private final ITriggerManager[] managers;

    public AbstractIrcControl(ITriggerManager... managers) {
        this.managers = managers;
    }

    /**
     * @see net.mauhiz.irc.base.IIrcControl#decodeIrcRawMsg(java.lang.String, net.mauhiz.irc.base.io.IIrcIO)
     */
    @Override
    public void decodeIrcRawMsg(String raw, IIrcIO io) {
        IIrcServerPeer peer = io.getServerPeer();
        IIrcMessage msg = IrcDecoder.INSTANCE.buildFromRaw(peer, raw);

        // FIXME implement basic processing as a trigger manager
        if (process(msg, io) == MsgState.AVAILABLE) {
            for (ITriggerManager itm : managers) {
                MsgState state = itm.processMsg(msg, this); // specific client actions
                if (state != MsgState.AVAILABLE) {
                    break;
                }
            }
        }
    }

    /**
     * @return trigger manager
     */
    @Override
    public ITriggerManager[] getManagers() {
        return managers;
    }

    protected abstract MsgState process(IIrcMessage message, IIrcIO io);
}
