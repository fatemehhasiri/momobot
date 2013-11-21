package net.mauhiz.board.impl.common.data;

import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.PlayerType;

public class AbstractMove implements Move {
	private final PlayerType playerType;

	public AbstractMove(final PlayerType playerType) {
		this.playerType = playerType;
	}

	@Override
	public PlayerType getPlayerType() {
		return playerType;
	}
}
