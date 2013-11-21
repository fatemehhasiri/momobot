package net.mauhiz.board.remote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.mauhiz.board.model.data.Move;
import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.msg.Ctcp;

public class BoardManager {

	static class IrcOpponent {
		IIrcControl control;
		IrcUser opponentUser;
		IIrcServerPeer server;
	}

	private static final BoardManager INSTANCE = new BoardManager();

	public static BoardManager getInstance() {
		return INSTANCE;
	}

	private final Map<String, RemoteBoardAdapter> games = new HashMap<>();
	private final Map<String, Set<IrcOpponent>> opponents = new HashMap<>();

	public void receiveMove(final String gameId, final String moveStr) throws IOException {
		final RemoteBoardAdapter rba = games.get(gameId);

		if (rba != null) {
			rba.readMove(moveStr);
		}
	}

	public void sendMove(final RemoteBoardAdapter rba, final Move move) {
		final String gameId = findGame(rba);

		if (gameId != null) {
			for (final IrcOpponent opponent : opponents.get(gameId)) {
				final Ctcp msg = new CtcpMove(null, opponent.opponentUser, opponent.server, move);
				opponent.control.sendMsg(msg);
			}
		}
	}

	private String findGame(final RemoteBoardAdapter rba) {
		for (final Entry<String, RemoteBoardAdapter> ent : games.entrySet()) {
			if (ent.getValue().equals(rba)) {
				return ent.getKey();
			}

		}
		return null;
	}
}
