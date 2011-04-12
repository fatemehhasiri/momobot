package net.mauhiz.board.gui.remote;

import net.mauhiz.irc.base.data.IrcServer;
import net.mauhiz.irc.base.data.Target;
import net.mauhiz.irc.base.msg.Ctcp;

public class CtcpMove extends Ctcp {

    public CtcpMove(Target from1, Target to1, IrcServer server1, String message1) {
        super(from1, to1, server1, message1);
    }

    @Override
    protected String getCommand() {
        return "MOVE";
    }

}
