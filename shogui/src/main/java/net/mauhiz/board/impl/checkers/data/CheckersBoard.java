package net.mauhiz.board.impl.checkers.data;

import java.awt.Dimension;

import net.mauhiz.board.impl.common.data.AbstractBoard;
import net.mauhiz.board.impl.common.data.SquareImpl;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.Square;

/**
 * @author mauhiz
 */
public class CheckersBoard extends AbstractBoard {
	public static final int SIZE = 10;

	static Square getSkippedSquare(final Square from, final Square to) {
		if (isCornerSkip(from, to)) {
			return SquareImpl.getInstance((from.getX() + to.getX()) / 2, (from.getY() + to.getY()) / 2);
		}
		return null;
	}

	public CheckersBoard(final CheckersRule rule) {
		super(rule);
	}

	@Override
	public void applyMove(final Move move) {
		if (move instanceof NormalMove) {
			final Square from = ((NormalMove) move).getFrom();
			final Square to = ((NormalMove) move).getTo();
			movePiece(from, to);

			// capture
			setPieceAt(getSkippedSquare(from, to), null);
		}
	}

	@Override
	public CheckersBoard copy() {
		final CheckersBoard copy = new CheckersBoard(null);
		super.copyInto(copy);
		return copy;
	}

	@Override
	public CheckersPiece getPieceAt(final Square square) {
		return (CheckersPiece) super.getPieceAt(square);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(SIZE, SIZE);
	}
}
