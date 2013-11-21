package net.mauhiz.board.impl.checkers.data;

import net.mauhiz.board.model.data.PlayerType;

public enum CheckersPlayerType implements PlayerType {
	BLACK, WHITE;

	@Override
	public PlayerType other() {
		return this == BLACK ? WHITE : BLACK;
	}
}
