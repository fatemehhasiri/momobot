package net.mauhiz.board.remote;

import java.io.IOException;

import net.mauhiz.board.model.BoardIO;
import net.mauhiz.board.model.GameController;
import net.mauhiz.board.model.MoveReader;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.PlayerType;

import org.apache.commons.io.IOUtils;

public class RemoteBoardAdapter implements GameController {

	private final GameController localController;
	private final MoveReader reader;

	public RemoteBoardAdapter(final GameController localController, final MoveReader reader) {
		this.localController = localController;
		this.reader = reader;
	}

	@Override
	public BoardIO getBoardIO() {
		return localController.getBoardIO();
	}

	@Override
	public Game getGame() {
		return localController.getGame();
	}

	public void readMove(final String moveStr) throws IOException {
		reader.readNext(IOUtils.toInputStream(moveStr), getGame());
	}

	@Override
	public PlayerType receiveMove(final Move move) {
		BoardManager.getInstance().sendMove(this, move);
		return localController.receiveMove(move);
	}

}
