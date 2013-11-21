package net.mauhiz.board.remote;

import net.mauhiz.board.model.data.Move;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.Target;
import net.mauhiz.irc.base.msg.Ctcp;

public class CtcpMove extends Ctcp {

	public CtcpMove(final IIrcServerPeer server, final Target from, final Target to, final Move move) {
		super(server, from, to, move.toString());
	}

	@Override
	protected String getCommand() {
		return "MOVE";
	}

}
