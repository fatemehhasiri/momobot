package net.mauhiz.board.impl.chess;

import net.mauhiz.board.impl.common.data.AbstractMove;
import net.mauhiz.board.model.data.PlayerType;

public class Castle extends AbstractMove {
	private final boolean great;

	public Castle(final PlayerType playerType, final boolean great) {
		super(playerType);
		this.great = great;
	}

	public boolean isGreat() {
		return great;
	}
}
