package net.mauhiz.board.impl.go.data;

import net.mauhiz.board.model.data.PlayerType;

public enum GoPlayerType implements PlayerType {
	BLACK, WHITE;

	@Override
	public PlayerType other() {
		return this == BLACK ? WHITE : BLACK;
	}
}
