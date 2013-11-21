package net.mauhiz.board.impl.shogi;

import net.mauhiz.board.impl.common.controller.AbstractPocketGameController;
import net.mauhiz.board.impl.shogi.data.ShogiGame;
import net.mauhiz.board.impl.shogi.data.ShogiRule;
import net.mauhiz.board.model.BoardIO;
import net.mauhiz.board.model.data.NormalMove;

import org.apache.log4j.Logger;

public class ShogiGameController extends AbstractPocketGameController {

	private static final Logger LOG = Logger.getLogger(ShogiGameController.class);

	public ShogiGameController(final BoardIO display) {
		super(display);
	}

	public PromoteMove convertToPromotion(final NormalMove move) {
		LOG.trace("Generating promotion from normal move: " + move);
		return new PromoteMove(move);
	}

	@Override
	public ShogiGame getGame() {
		return (ShogiGame) game;
	}

	@Override
	protected ShogiGame newGame() {
		return new ShogiGame(new ShogiRule());
	}
}
