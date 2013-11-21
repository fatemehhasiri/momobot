package net.mauhiz.board.model.data;

public interface Rule {
	Move generateMove(Square from, Square to, Game game);

	PlayerType[] getPlayerTypes();

	PlayerType getStartingPlayer();

	void initPieces(Board board);

	Board newBoard();

	boolean postCheck(Move move, Board newBoard, Game game);

	boolean preCheck(Move move, Board oldBoard, Game game);
}
