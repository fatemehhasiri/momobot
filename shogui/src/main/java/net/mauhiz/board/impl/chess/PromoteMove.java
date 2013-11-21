package net.mauhiz.board.impl.chess;

import net.mauhiz.board.impl.chess.data.ChessPieceType;
import net.mauhiz.board.impl.common.data.AbstractMove;
import net.mauhiz.board.model.data.NormalMove;

public class PromoteMove extends AbstractMove {
	private final NormalMove parentMove;
	private final ChessPieceType promotion;

	public PromoteMove(final NormalMove parentMove, final ChessPieceType promotion) {
		super(parentMove.getPlayerType());
		this.parentMove = parentMove;
		this.promotion = promotion;
	}

	public NormalMove getParentMove() {
		return parentMove;
	}

	public ChessPieceType getPromotion() {
		return promotion;
	}
}
