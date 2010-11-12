package net.mauhiz.irc.base;

import net.mauhiz.irc.base.msg.IIrcMessage;

/**
 * @author mauhiz
 */
public interface ITriggerManager {
    void processMsg(IIrcMessage msg, IIrcControl ircControl);
    void shutdown();
}
