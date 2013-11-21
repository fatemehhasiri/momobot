package net.mauhiz.board.model.data;

public interface Drop extends Move {
	PieceType getPieceType();

	Square getTo();
}
