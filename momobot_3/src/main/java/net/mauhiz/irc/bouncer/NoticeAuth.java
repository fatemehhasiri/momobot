package net.mauhiz.irc.bouncer;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.msg.AbstractIrcMessage;

public class NoticeAuth extends AbstractIrcMessage {
    private final String msg;

    public NoticeAuth(IIrcServerPeer server, String msg) {
        super(server, null);
        this.msg = msg;
    }

    @Override
    public NoticeAuth copy() {
        return new NoticeAuth(server, msg);
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.NOTICE; // not a real IRC command anyways 
    }

    @Override
    public String getIrcForm() {
        return "NOTICE AUTH :" + msg;
    }
}
