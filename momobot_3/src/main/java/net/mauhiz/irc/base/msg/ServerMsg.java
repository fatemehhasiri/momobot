package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.Target;

/**
 * @author mauhiz
 */
public class ServerMsg extends AbstractIrcMessage {
    private final ArgumentList args;
    /**
     * code numerique
     */
    private final int code;
    /**
     * suite du message
     */
    private final String msg;

    public ServerMsg(IIrcServerPeer server, Target from, int code, ArgumentList args, String msg) {
        super(server, from);
        this.code = code;
        this.msg = msg;
        this.args = args;
    }

    @Override
    public IIrcMessage copy() {
        return new ServerMsg(server, from, code, args.copy(), msg);
    }

    public ArgumentList getArgs() {
        return args.copy();
    }

    /**
     * @return {@link #code}
     */
    public int getCode() {
        return code;
    }

    @Override
    public IrcCommands getIrcCommand() {
        return null; // uses numeric replies
    }

    @Override
    public String getIrcForm() {
        return ircFromDisplay() + code + " " + args.getRemainingData() + " :" + msg;
    }

    /**
     * @return {@link #msg}
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @see net.mauhiz.irc.base.msg.AbstractIrcMessage#toString()
     */
    @Override
    public String toString() {
        return getIrcForm();
    }
}
