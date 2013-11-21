package net.mauhiz.board.impl.common.data;

import net.mauhiz.board.model.data.Board;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Rule;
import net.mauhiz.board.model.data.Square;

import org.apache.commons.lang.ObjectUtils;

public abstract class AbstractRule implements Rule {

	@Override
	public Move generateMove(final Square from, final Square to, final Game game) {
		if (!ObjectUtils.equals(to, from)) {
			final Board lastBoard = game.getLastBoard();
			NormalMove move;
			final Piece onTarget = lastBoard.getPieceAt(to);
			if (onTarget == null) {
				move = new NormalMoveImpl(game.getTurn(), from, to);
			} else {
				move = new CaptureMove(game.getTurn(), from, to, onTarget.getPieceType());
			}
			if (preCheck(move, lastBoard, game)) {
				return move;
			}
		}

		return null;
	}

	protected abstract boolean isForward(Square from, Square to, PlayerType player);

	protected boolean isFrontCorner(final Square from, final Square to, final PlayerType player) {
		return AbstractBoard.isCorner(from, to) && isForward(from, to, player);
	}
}
