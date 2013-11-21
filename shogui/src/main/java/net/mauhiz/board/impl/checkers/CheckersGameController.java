package net.mauhiz.board.impl.checkers;

import net.mauhiz.board.impl.checkers.data.CheckersGame;
import net.mauhiz.board.impl.checkers.data.CheckersRule;
import net.mauhiz.board.impl.common.controller.AbstractGameController;
import net.mauhiz.board.model.BoardIO;
import net.mauhiz.board.model.data.Game;

public class CheckersGameController extends AbstractGameController {

	public CheckersGameController(final BoardIO display) {
		super(display);
	}

	@Override
	protected Game newGame() {
		return new CheckersGame(new CheckersRule());
	}
}
