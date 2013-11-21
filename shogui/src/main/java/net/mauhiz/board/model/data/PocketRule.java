package net.mauhiz.board.model.data;

public interface PocketRule extends Rule {

	Drop generateMove(PieceType toDrop, Square onTo, Game game);
}
