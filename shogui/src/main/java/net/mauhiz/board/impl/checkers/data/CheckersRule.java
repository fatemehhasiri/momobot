package net.mauhiz.board.impl.checkers.data;

import net.mauhiz.board.impl.common.data.AbstractBoard;
import net.mauhiz.board.impl.common.data.AbstractRule;
import net.mauhiz.board.model.data.Board;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;

public class CheckersRule extends AbstractRule {

	@Override
	public PlayerType[] getPlayerTypes() {
		return CheckersPlayerType.values();
	}

	@Override
	public PlayerType getStartingPlayer() {
		return CheckersPlayerType.BLACK;
	}

	@Override
	public void initPieces(final Board board) {
		for (final Square square : board.getSquares()) {
			final int j = square.getY();
			final int i = square.getX();

			if (j <= 3 || j >= 6) {
				final CheckersPlayerType pl = j <= 4 ? CheckersPlayerType.BLACK : CheckersPlayerType.WHITE;

				if ((i + j) % 2 == 0) {
					board.setPieceAt(square, new CheckersPiece(pl, CheckersPieceType.PAWN));
				}
			}
		}
	}

	@Override
	public CheckersBoard newBoard() {
		return new CheckersBoard(this);
	}

	@Override
	public boolean postCheck(final Move move, final Board newBoard, final Game game) {
		return true;
	}

	@Override
	public boolean preCheck(final Move move, final Board oldBoard, final Game game) {
		if (move instanceof NormalMove) {
			final NormalMove nmove = (NormalMove) move;
			return canGo(oldBoard, nmove.getFrom(), nmove.getTo());
		}

		return false;
	}

	boolean canPromote(final NormalMove nmove) {
		return nmove.getPlayerType() == CheckersPlayerType.BLACK ? nmove.getTo().getY() == 9
				: nmove.getTo().getY() == 0;
	}

	@Override
	protected boolean isForward(final Square from, final Square to, final PlayerType player) {
		return from.getY() != to.getY() && player == CheckersPlayerType.BLACK ^ from.getY() > to.getY();
	}

	/**
	 * @param from
	 * @param to
	 *            is different from 'from'
	 * @return
	 */
	private boolean canGo(final Board b, final Square from, final Square to) {
		final Piece op = b.getPieceAt(from);

		if (b.getPieceAt(to) != null) {
			return false;
		}

		if (AbstractBoard.isCornerSkip(from, to)) {
			// capture
			final Square skipped = CheckersBoard.getSkippedSquare(from, to);
			final Piece captured = b.getPieceAt(skipped);
			if (captured == null || captured.getPlayerType() == op.getPlayerType()) {
				return false;
			}

		} else if (!AbstractBoard.isCorner(from, to)) {
			return false;
		}

		return isForward(from, to, op.getPlayerType()) || op.getPieceType() == CheckersPieceType.QUEEN;
	}
}
