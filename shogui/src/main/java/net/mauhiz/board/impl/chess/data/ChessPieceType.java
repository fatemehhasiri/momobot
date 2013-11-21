package net.mauhiz.board.impl.chess.data;

import net.mauhiz.board.model.data.PieceType;

public enum ChessPieceType implements PieceType {
	BISHOP, KING, KNIGHT {
		@Override
		public String getName() {
			return "N";
		}
	},
	PAWN, QUEEN, ROOK;

	public static ChessPieceType fromName(final String name) {
		for (final ChessPieceType piece : values()) {
			if (piece.getName().equals(name)) {
				return piece;
			}
		}
		return null;
	}

	public static ChessPieceType[] getPromotions() {
		return new ChessPieceType[] { QUEEN, ROOK, BISHOP, KNIGHT };
	}

	@Override
	public String getName() {
		return name().substring(0, 1);
	}

	@Override
	public String toString() {
		return getName();
	}
}
