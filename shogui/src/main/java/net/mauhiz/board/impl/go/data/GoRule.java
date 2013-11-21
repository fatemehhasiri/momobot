package net.mauhiz.board.impl.go.data;

import net.mauhiz.board.impl.common.data.AbstractPocketRule;
import net.mauhiz.board.model.data.Board;
import net.mauhiz.board.model.data.Drop;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;

public class GoRule extends AbstractPocketRule {

	@Override
	public GoPlayerType[] getPlayerTypes() {
		return GoPlayerType.values();
	}

	@Override
	public GoPlayerType getStartingPlayer() {
		return GoPlayerType.BLACK;
	}

	@Override
	public void initPieces(final Board board) {
		// starts with empty board
	}

	@Override
	public GoBoard newBoard() {
		return new GoBoard(this);
	}

	@Override
	public boolean postCheck(final Move move, final Board newBoard, final Game game) {
		return true;
	}

	@Override
	public boolean preCheck(final Move move, final Board oldBoard, final Game game) {
		if (move.getPlayerType() != game.getTurn()) {
			return false;
		}
		if (move instanceof Drop) {
			return canDrop(oldBoard, ((Drop) move).getTo());
		}
		return false;
	}

	protected boolean canDrop(final Board board, final Square to) {
		return board.getPieceAt(to) == null;
	}

	@Override
	protected boolean isForward(final Square from, final Square to, final PlayerType player) {
		return false;
	}
}
