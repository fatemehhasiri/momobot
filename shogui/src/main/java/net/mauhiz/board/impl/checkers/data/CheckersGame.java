package net.mauhiz.board.impl.checkers.data;

import net.mauhiz.board.impl.checkers.PromoteMove;
import net.mauhiz.board.impl.common.data.AbstractGame;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;

public class CheckersGame extends AbstractGame {

	public CheckersGame(final CheckersRule rule) {
		super(rule);
	}

	@Override
	public CheckersPlayerType applyMove(final Move move) {
		Move realMove = move;

		if (move instanceof NormalMove) {
			final NormalMove nmove = (NormalMove) move;
			if (getRule().canPromote(nmove)) {
				realMove = new PromoteMove(nmove);
			}
		}
		return (CheckersPlayerType) super.applyMove(realMove);
	}

	@Override
	public CheckersRule getRule() {
		return (CheckersRule) super.getRule();
	}

	@Override
	public CheckersPlayerType getTurn() {
		// TODO can capture again with same piece if capture
		return (CheckersPlayerType) super.getTurn();
	}

}
