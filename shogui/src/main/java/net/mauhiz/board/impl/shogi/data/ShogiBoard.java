package net.mauhiz.board.impl.shogi.data;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mauhiz.board.impl.common.data.AbstractBoard;
import net.mauhiz.board.impl.shogi.PromoteMove;
import net.mauhiz.board.model.data.Drop;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.PocketBoard;
import net.mauhiz.board.model.data.Square;
import net.mauhiz.util.PerformanceMonitor;

import org.apache.log4j.Logger;

public class ShogiBoard extends AbstractBoard implements PocketBoard {
	public static final int SIZE = 9;
	private static final Logger LOG = Logger.getLogger(ShogiBoard.class);
	protected final Map<ShogiPlayerType, List<ShogiPieceType>> pockets = new HashMap<>(2);

	public ShogiBoard(final ShogiRule rule) {
		super(rule);
		for (final ShogiPlayerType spt : ShogiPlayerType.values()) {
			pockets.put(spt, new ArrayList<ShogiPieceType>());
		}
	}

	@Override
	public void applyMove(final Move move) {
		if (move instanceof Drop) {
			final PerformanceMonitor pm = new PerformanceMonitor();
			final Drop drop = (Drop) move;
			final ShogiPieceType pieceType = (ShogiPieceType) drop.getPieceType();
			final ShogiPlayerType playerType = (ShogiPlayerType) drop.getPlayerType();
			if (pockets.get(playerType).remove(pieceType)) {
				setPieceAt(drop.getTo(), new ShogiPiece(playerType, pieceType));
			} else {
				LOG.warn("Player: " + playerType + " had no such piece: " + pieceType + " in his pocket");
			}
			pm.perfLog("Drop applied: " + drop, getClass());

		} else if (move instanceof NormalMove) {
			final PerformanceMonitor pm = new PerformanceMonitor();
			final NormalMove nmove = (NormalMove) move;
			final ShogiPiece capturedPiece = (ShogiPiece) movePiece(nmove.getFrom(), nmove.getTo());

			if (capturedPiece != null) {
				final ShogiPieceType capturedPieceType = capturedPiece.getPieceType().reversePromotion();
				LOG.debug("Piece captured: " + capturedPiece);
				final List<ShogiPieceType> pocket = pockets.get(nmove.getPlayerType());
				pocket.add(capturedPieceType);
				Collections.sort(pocket);
				LOG.trace("New pocket: " + pocket);
			}
			pm.perfLog("Normal move applied: " + nmove, getClass());
		} else if (move instanceof PromoteMove) {
			final PerformanceMonitor pm = new PerformanceMonitor();
			final PromoteMove pmove = (PromoteMove) move;
			final NormalMove parentMove = pmove.getParentMove();
			applyMove(parentMove);
			final ShogiPiece moved = setPieceAt(parentMove.getTo(), null);
			setPieceAt(parentMove.getTo(), new ShogiPiece((ShogiPlayerType) pmove.getPlayerType(), moved.getPieceType()
					.getPromotion()));
			pm.perfLog("Promote move applied: " + pmove, getClass());
		}
	}

	@Override
	public ShogiBoard copy() {
		final ShogiBoard copy = new ShogiBoard(null);
		super.copyInto(copy);
		for (final Entry<ShogiPlayerType, List<ShogiPieceType>> ent : pockets.entrySet()) {
			copy.pockets.put(ent.getKey(), new ArrayList<>(ent.getValue()));
		}
		return copy;
	}

	public Collection<ShogiPiece> getAllPocketPieces() {
		final List<ShogiPiece> pieces = new ArrayList<>();
		for (final Entry<ShogiPlayerType, List<ShogiPieceType>> ent : pockets.entrySet()) {
			final ShogiPlayerType playerType = ent.getKey();

			for (final ShogiPieceType pt : ent.getValue()) {
				pieces.add(new ShogiPiece(playerType, pt));
			}
		}

		return pieces;
	}

	@Override
	public ShogiPiece getPieceAt(final Square square) {
		return (ShogiPiece) super.getPieceAt(square);
	}

	@Override
	public List<ShogiPieceType> getPocket(final PlayerType player) {
		return pockets.get(player);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(SIZE, SIZE);
	}

	public boolean hasPawnOnColumn(final ShogiPlayerType pl, final int x) {
		for (final Square square : getSquares()) {
			if (square.getX() != x) {
				continue;
			}
			final ShogiPiece op = getPieceAt(square);
			if (op == null) {
				continue;
			}
			if (op.getPlayerType() == pl && op.getPieceType() == ShogiPieceType.PAWN) {
				return true;
			}
		}

		return false;
	}

	@Override
	public ShogiPiece setPieceAt(final Square square, final Piece piece) {
		return (ShogiPiece) super.setPieceAt(square, piece);
	}
}
