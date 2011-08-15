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

	public static int getXmove(Square from, Square to) {
		return to.getX() - from.getX();
	}

	public static int getYmove(Square from, Square to) {
		return to.getY() - from.getY();
	}

	public static boolean isCorner(Square from, Square to) {
		return abs(getXmove(from, to)) == 1 && abs(getYmove(from, to)) == 1;
	}

	public static boolean isCornerSkip(Square from, Square to) {
		return abs(getXmove(from, to)) == 2 && abs(getYmove(from, to)) == 2;
	}

	public static boolean isCross(Square from, Square to) {
		return abs(getXmove(from, to)) == 1 && abs(getYmove(from, to)) == 0 || abs(getXmove(from, to)) == 0
				&& abs(getYmove(from, to)) == 1;
	}

	public static boolean isDiagonal(Square from, Square to) {
		return abs(getXmove(from, to)) == abs(getYmove(from, to));
	}

	public static boolean isDownToUp(Square from, Square to) {
		return to.getY() > from.getY();
	}

	public static boolean isHorizontal(Square from, Square to) {
		return getYmove(from, to) == 0;
	}

	public static boolean isLeftToRight(Square from, Square to) {
		return to.getX() > from.getX();
	}

	public static boolean isStraight(Square from, Square to) {
		return isHorizontal(from, to) || isVertical(from, to);
	}

	public static boolean isVertical(Square from, Square to) {
		return getXmove(from, to) == 0;
	}

	private final Map<Square, Piece> piecesMap = new HashMap<Square, Piece>();

	public AbstractBoard(Rule rule) {
		super();
		if (rule != null) {
			rule.initPieces(this);
		}
	}

	protected void copyInto(AbstractBoard copy) {
		synchronized (piecesMap) {
			copy.piecesMap.putAll(piecesMap);
		}
	}

	public Square findSquare(PlayerType player, PieceType piece) {
		for (Square square : getSquares()) {
			Piece op = getPieceAt(square);
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

	protected Piece getPieceAt(int i, int j) {
		return getPieceAt(SquareImpl.getInstance(i, j));
	}

	public Piece getPieceAt(Square square) {
		synchronized (piecesMap) {
			return piecesMap.get(square);
		}
	}

	public Iterable<Square> getSquares() {
		return new SquareView(getSize());
	}

	public boolean isFriendlyPieceOn(PlayerType pl, Square to) {
		Piece op = getPieceAt(to);
		return op != null && op.getPlayerType().equals(pl);
	}

	public boolean isObstruction(Square from, Square to) {
		if (isDiagonal(from, to)) {
			boolean positiveCoef = isDownToUp(from, to) ^ !isLeftToRight(from, to);
			int minX = Math.min(from.getX(), to.getX());
			int maxX = Math.max(from.getX(), to.getX());

			int minY = Math.min(from.getY(), to.getY());
			int maxY = Math.max(from.getY(), to.getY());

			for (int i = 1; i < maxX - minX; i++) {
				if (getPieceAt(minX + i, positiveCoef ? minY + i : maxY - i) != null) {
					return true;
				}
			}
			return false;
		} else if (isHorizontal(from, to)) {
			int horIncr = isLeftToRight(from, to) ? 1 : -1;
			for (int i = from.getX() + horIncr; i != to.getX(); i += horIncr) {
				if (getPieceAt(i, to.getY()) != null) {
					return true;
				}
			}
			return false;
		} else if (isVertical(from, to)) {
			int vertIncr = isDownToUp(from, to) ? 1 : -1;
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

	public Piece movePiece(Square from, Square to) {
		synchronized (piecesMap) {
			return piecesMap.put(to, piecesMap.remove(from));
		}
	}

	public Piece setPieceAt(Square square, Piece piece) {
		synchronized (piecesMap) {
			if (piece == null) {
				return piecesMap.remove(square);
			}
			return piecesMap.put(square, piece);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Square square : getSquares()) {
			sb.append("[").append(getPieceAt(square)).append("] ");
		}
		return sb.toString();
	}
}