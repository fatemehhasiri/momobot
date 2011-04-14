package net.mauhiz.irc.base.data;

import net.mauhiz.irc.base.msg.Ctcp;
import net.mauhiz.irc.base.msg.Action;
import net.mauhiz.irc.base.msg.CtcpVersion;
import net.mauhiz.irc.base.msg.DccChatRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class CtcpFactory {

    public static Ctcp decode(Target from, Target to, IIrcServerPeer server, String msg) {
        final String command = StringUtils.substringBefore(msg, " ");
        String ctcpContent = StringUtils.substringAfter(msg, " ");

        if (Action.COMMAND.equals(command)) {
            return new Action(from, to, server, ctcpContent);

        } else if (CtcpVersion.COMMAND.equals(command)) {
            return new CtcpVersion(from, to, server, ctcpContent);

        } else if (DccChatRequest.COMMAND.equals(command)) {
            if (ctcpContent.startsWith("CHAT CHAT ")) {
                return new DccChatRequest(from, to, server, ctcpContent);
            }
        }

        Logger.getLogger(CtcpFactory.class).warn("Unknwon CTCP: " + command);
        return new Ctcp(from, to, server, ctcpContent) {

            @Override
            protected String getCommand() {
                return command;
            }
        };
    }
}
