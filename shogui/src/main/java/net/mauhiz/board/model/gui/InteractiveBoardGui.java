package net.mauhiz.board.model.gui;

import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.Square;

public interface InteractiveBoardGui extends BoardGui {

	void addCancelAction(Square square);

	void addMoveAction(Square square, Move move);

	void addSelectAction(Square square);

	void cancelSelection();

	void disableSquare(Square square);

	Square getSelectedSquare();

	/**
	 * Selects a square on the board
	 * @param at
	 */
	void selectSquare(Square at);
}
