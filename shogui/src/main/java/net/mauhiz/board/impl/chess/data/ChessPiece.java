package net.mauhiz.board.impl.chess.data;

import net.mauhiz.board.impl.common.data.DefaultPiece;

public class ChessPiece extends DefaultPiece {

	public ChessPiece(final ChessPlayerType playerType, final ChessPieceType pieceType) {
		super(playerType, pieceType);
	}

	@Override
	public ChessPieceType getPieceType() {
		return (ChessPieceType) super.getPieceType();
	}

	@Override
	public ChessPlayerType getPlayerType() {
		return (ChessPlayerType) super.getPlayerType();
	}
}
