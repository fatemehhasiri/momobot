package net.mauhiz.board.impl.shogi.data;

import net.mauhiz.board.impl.common.data.DefaultPiece;

public class ShogiPiece extends DefaultPiece {

	public ShogiPiece(final ShogiPlayerType playerType, final ShogiPieceType pieceType) {
		super(playerType, pieceType);
	}

	@Override
	public ShogiPieceType getPieceType() {
		return (ShogiPieceType) super.getPieceType();
	}

	@Override
	public ShogiPlayerType getPlayerType() {
		return (ShogiPlayerType) super.getPlayerType();
	}
}
