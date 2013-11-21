package net.mauhiz.board.impl.common.data;

import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.PlayerType;

public class InitMove implements Move {

	private final PlayerType starter;

	public InitMove(final PlayerType starter) {
		this.starter = starter;
	}

	@Override
	public PlayerType getPlayerType() {
		return starter;
	}

	@Override
	public String toString() {
		return "start";
	}
}
