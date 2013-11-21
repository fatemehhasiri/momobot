package net.mauhiz.board.impl.chess.data;

import java.awt.Dimension;

import net.mauhiz.board.impl.chess.Castle;
import net.mauhiz.board.impl.chess.EnPassant;
import net.mauhiz.board.impl.chess.PromoteMove;
import net.mauhiz.board.impl.common.data.AbstractBoard;
import net.mauhiz.board.impl.common.data.SquareImpl;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Rule;
import net.mauhiz.board.model.data.Square;

public class ChessBoard extends AbstractBoard {
	public static enum Status {
		CHECK(false), DRAW(true), MATE(true);
		private final boolean end;

		private Status(final boolean end) {
			this.end = end;
		}

		public boolean isEnd() {
			return end;
		}
	}

	public static final int SIZE = 8;

	public ChessBoard(final Rule rule) {
		super(rule);
	}

	@Override
	public void applyMove(final Move move) {
		if (move instanceof EnPassant) {
			final EnPassant passant = (EnPassant) move;
			final Square from = passant.getFrom();
			final Square to = passant.getTo();
			final Square enPassant = SquareImpl.getInstance(to.getX(), from.getY());
			setPieceAt(enPassant, null);
		}
		if (move instanceof NormalMove) {
			final Square from = ((NormalMove) move).getFrom();
			final Square to = ((NormalMove) move).getTo();
			movePiece(from, to);

		} else if (move instanceof Castle) {
			final Castle castle = (Castle) move;
			final int row = move.getPlayerType() == ChessPlayerType.WHITE ? 0 : 7;
			final Square kingFrom = SquareImpl.getInstance(4, row);
			final Square kingTo = SquareImpl.getInstance(castle.isGreat() ? 2 : 6, row);
			movePiece(kingFrom, kingTo);

			// move the rook too
			final Square rookFrom = SquareImpl.getInstance(castle.isGreat() ? 0 : 7, row);
			final Square rookTo = SquareImpl.getInstance(castle.isGreat() ? 3 : 5, row);
			movePiece(rookFrom, rookTo);
		} else if (move instanceof PromoteMove) {
			final PromoteMove pmove = (PromoteMove) move;
			final NormalMove parentMove = pmove.getParentMove();
			applyMove(parentMove);
			setPieceAt(parentMove.getTo(),
					new ChessPiece((ChessPlayerType) pmove.getPlayerType(), pmove.getPromotion()));
		}
	}

	@Override
	public ChessBoard copy() {
		final ChessBoard copy = new ChessBoard(null);
		super.copyInto(copy);
		return copy;
	}

	@Override
	public ChessPiece getPieceAt(final Square square) {
		return (ChessPiece) super.getPieceAt(square);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(SIZE, SIZE);
	}

	protected Square findKingSquare(final PlayerType pl) {
		return findSquare(pl, ChessPieceType.KING);
	}
}
