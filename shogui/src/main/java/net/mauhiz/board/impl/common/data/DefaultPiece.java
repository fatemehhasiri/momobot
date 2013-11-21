package net.mauhiz.board.impl.common.data;

import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;

public class DefaultPiece implements Piece {
	private final PieceType pieceType;
	private final PlayerType playerType;

	public DefaultPiece(final PlayerType playerType, final PieceType pieceType) {
		super();
		this.playerType = playerType;
		this.pieceType = pieceType;
	}

	@Override
	public PieceType getPieceType() {
		return pieceType;
	}

	@Override
	public PlayerType getPlayerType() {
		return playerType;
	}

	@Override
	public String toString() {
		return playerType.toString().substring(0, 1) + "." + pieceType;
	}
}
