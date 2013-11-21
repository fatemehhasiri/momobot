package net.mauhiz.board.impl.common.data;

import static java.lang.Math.abs;

import java.util.HashMap;
import java.util.Map;

import net.mauhiz.board.model.data.Board;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Rule;
import net.mauhiz.board.model.data.Square;

import org.apache.log4j.Logger;

public abstract class AbstractBoard implements Board {

	private static final Logger LOG = Logger.getLogger(AbstractBoard.class);

	public static int getXmove(final Square from, final Square to) {
		return to.getX() - from.getX();
	}

	public static int getYmove(final Square from, final Square to) {
		return to.getY() - from.getY();
	}

	public static boolean isCorner(final Square from, final Square to) {
		return abs(getXmove(from, to)) == 1 && abs(getYmove(from, to)) == 1;
	}

	public static boolean isCornerSkip(final Square from, final Square to) {
		return abs(getXmove(from, to)) == 2 && abs(getYmove(from, to)) == 2;
	}

	public static boolean isCross(final Square from, final Square to) {
		return abs(getXmove(from, to)) == 1 && abs(getYmove(from, to)) == 0 || abs(getXmove(from, to)) == 0
				&& abs(getYmove(from, to)) == 1;
	}

	public static boolean isDiagonal(final Square from, final Square to) {
		return abs(getXmove(from, to)) == abs(getYmove(from, to));
	}

	public static boolean isDownToUp(final Square from, final Square to) {
		return to.getY() > from.getY();
	}

	public static boolean isHorizontal(final Square from, final Square to) {
		return getYmove(from, to) == 0;
	}

	public static boolean isLeftToRight(final Square from, final Square to) {
		return to.getX() > from.getX();
	}

	public static boolean isStraight(final Square from, final Square to) {
		return isHorizontal(from, to) || isVertical(from, to);
	}

	public static boolean isVertical(final Square from, final Square to) {
		return getXmove(from, to) == 0;
	}

	private final Map<Square, Piece> piecesMap = new HashMap<>();

	public AbstractBoard(final Rule rule) {
		super();
		if (rule != null) {
			rule.initPieces(this);
		}
	}

	@Override
	public Square findSquare(final PlayerType player, final PieceType piece) {
		for (final Square square : getSquares()) {
			final Piece op = getPieceAt(square);
			if (op == null) {
				continue;
			}
			if (op.getPlayerType() == player && op.getPieceType() == piece) {
				return square;
			}
		}
		LOG.warn(piece + " not found for player: " + player);
		return null;
	}

	@Override
	public Piece getPieceAt(final Square square) {
		synchronized (piecesMap) {
			return piecesMap.get(square);
		}
	}

	@Override
	public Iterable<Square> getSquares() {
		return new SquareView(getSize());
	}

	@Override
	public boolean isFriendlyPieceOn(final PlayerType pl, final Square to) {
		final Piece op = getPieceAt(to);
		return op != null && op.getPlayerType().equals(pl);
	}

	@Override
	public boolean isObstruction(final Square from, final Square to) {
		if (isDiagonal(from, to)) {
			final boolean positiveCoef = isDownToUp(from, to) ^ !isLeftToRight(from, to);
			final int minX = Math.min(from.getX(), to.getX());
			final int maxX = Math.max(from.getX(), to.getX());

			final int minY = Math.min(from.getY(), to.getY());
			final int maxY = Math.max(from.getY(), to.getY());

			for (int i = 1; i < maxX - minX; i++) {
				if (getPieceAt(minX + i, positiveCoef ? minY + i : maxY - i) != null) {
					return true;
				}
			}
			return false;
		} else if (isHorizontal(from, to)) {
			final int horIncr = isLeftToRight(from, to) ? 1 : -1;
			for (int i = from.getX() + horIncr; i != to.getX(); i += horIncr) {
				if (getPieceAt(i, to.getY()) != null) {
					return true;
				}
			}
			return false;
		} else if (isVertical(from, to)) {
			final int vertIncr = isDownToUp(from, to) ? 1 : -1;
			for (int j = from.getY() + vertIncr; j != to.getY(); j += vertIncr) {
				if (getPieceAt(to.getX(), j) != null) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public Piece movePiece(final Square from, final Square to) {
		synchronized (piecesMap) {
			return piecesMap.put(to, piecesMap.remove(from));
		}
	}

	@Override
	public Piece setPieceAt(final Square square, final Piece piece) {
		synchronized (piecesMap) {
			if (piece == null) {
				return piecesMap.remove(square);
			}
			return piecesMap.put(square, piece);
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Square square : getSquares()) {
			sb.append("[").append(getPieceAt(square)).append("] ");
		}
		return sb.toString();
	}

	protected void copyInto(final AbstractBoard copy) {
		synchronized (piecesMap) {
			copy.piecesMap.putAll(piecesMap);
		}
	}

	protected Piece getPieceAt(final int i, final int j) {
		return getPieceAt(SquareImpl.getInstance(i, j));
	}
}
