package net.mauhiz.board.impl.checkers.data;

import net.mauhiz.board.impl.common.data.DefaultPiece;

public class CheckersPiece extends DefaultPiece {

	public CheckersPiece(final CheckersPlayerType playerType, final CheckersPieceType pieceType) {
		super(playerType, pieceType);
	}

	@Override
	public CheckersPieceType getPieceType() {
		return (CheckersPieceType) super.getPieceType();
	}

	@Override
	public CheckersPlayerType getPlayerType() {
		return (CheckersPlayerType) super.getPlayerType();
	}
}
