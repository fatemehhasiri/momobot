package net.mauhiz.board.impl.common.controller;

import net.mauhiz.board.model.BoardIO;
import net.mauhiz.board.model.GameController;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.PlayerType;

public abstract class AbstractGameController implements GameController {
	protected Game game;
	private final BoardIO boardIO;

	//	private Map<PlayerType, Player> players = new TreeMap<PlayerType, Player>();

	protected AbstractGameController(final BoardIO display) {
		super();
		boardIO = display;
		game = newGame();
	}

	@Override
	public BoardIO getBoardIO() {
		return boardIO;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public PlayerType receiveMove(final Move move) {
		return game.applyMove(move);
	}

	protected abstract Game newGame();
}
