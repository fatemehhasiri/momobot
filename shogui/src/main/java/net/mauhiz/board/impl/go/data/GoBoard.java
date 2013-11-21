package net.mauhiz.board.impl.go.data;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mauhiz.board.impl.common.data.AbstractBoard;
import net.mauhiz.board.model.data.Drop;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.PocketBoard;
import net.mauhiz.board.model.data.Square;

public class GoBoard extends AbstractBoard implements PocketBoard {
	protected final Map<GoPlayerType, List<GoPieceType>> captures = new HashMap<>(2);

	public GoBoard(final GoRule rule) {
		super(rule);
		for (final GoPlayerType spt : GoPlayerType.values()) {
			captures.put(spt, new ArrayList<GoPieceType>());
		}
	}

	@Override
	public void applyMove(final Move move) {
		if (move instanceof Drop) {
			final Drop drop = (Drop) move;
			final GoPieceType pieceType = (GoPieceType) drop.getPieceType();
			final GoPlayerType playerType = (GoPlayerType) drop.getPlayerType();
			setPieceAt(drop.getTo(), new GoPiece(playerType, pieceType));
		}
	}

	@Override
	public GoBoard copy() {
		final GoBoard copy = new GoBoard(null);
		super.copyInto(copy);
		for (final Entry<GoPlayerType, List<GoPieceType>> ent : captures.entrySet()) {
			copy.captures.put(ent.getKey(), new ArrayList<>(ent.getValue()));
		}
		return copy;
	}

	public Collection<GoPiece> getAllPocketPieces() {
		final List<GoPiece> pieces = new ArrayList<>();
		for (final GoPlayerType playerType : GoPlayerType.values()) {
			pieces.add(new GoPiece(playerType, GoPieceType.STONE));
		}

		return pieces;
	}

	@Override
	public GoPiece getPieceAt(final Square square) {
		return (GoPiece) super.getPieceAt(square);
	}

	@Override
	public List<GoPieceType> getPocket(final PlayerType player) {
		return Collections.singletonList(GoPieceType.STONE);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(19, 19);
	}

	@Override
	public GoPiece setPieceAt(final Square square, final Piece piece) {
		return (GoPiece) super.setPieceAt(square, piece);
	}
}
