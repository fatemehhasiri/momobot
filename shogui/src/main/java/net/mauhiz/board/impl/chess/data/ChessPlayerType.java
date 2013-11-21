package net.mauhiz.board.impl.chess.data;

import net.mauhiz.board.model.data.PlayerType;

public enum ChessPlayerType implements PlayerType {
	BLACK, WHITE;

	@Override
	public ChessPlayerType other() {
		return this == BLACK ? WHITE : BLACK;
	}
}
