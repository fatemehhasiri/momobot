package net.mauhiz.board.impl.checkers;

import net.mauhiz.board.impl.common.data.AbstractMove;
import net.mauhiz.board.model.data.NormalMove;

public class PromoteMove extends AbstractMove {
	private final NormalMove parentMove;

	public PromoteMove(NormalMove parentMove) {
		super(parentMove.getPlayerType());
		this.parentMove = parentMove;
	}

	public NormalMove getParentMove() {
		return parentMove;
	}
}
