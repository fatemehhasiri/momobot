package net.mauhiz.board.impl.chess;

import net.mauhiz.board.impl.chess.data.ChessGame;
import net.mauhiz.board.impl.chess.data.ChessRule;
import net.mauhiz.board.impl.common.controller.AbstractGameController;
import net.mauhiz.board.model.BoardIO;
import net.mauhiz.board.model.data.Game;

public class ChessGameController extends AbstractGameController {
	public ChessGameController(final BoardIO display) {
		super(display);
	}

	@Override
	protected Game newGame() {
		return new ChessGame(new ChessRule());
	}
}
