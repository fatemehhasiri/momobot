package net.mauhiz.irc.base;

import net.mauhiz.irc.base.io.IIrcIO;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.trigger.ITriggerManager;

/**
 * @author mauhiz
 */
public interface IIrcControl extends AutoCloseable {

    @Override
    void close();

    /**
     * @param raw
     *            NOT NULL
     * @param io
     */
    void decodeIrcRawMsg(String raw, IIrcIO io);

    ITriggerManager[] getManagers();

    /**
     * @param msg
     */
    void sendMsg(IIrcMessage msg);
}
