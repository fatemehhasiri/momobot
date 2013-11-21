package net.mauhiz.board.impl.common.data;

import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;

public class CaptureMove extends NormalMoveImpl {

	private final PieceType captured;

	public CaptureMove(final PlayerType playerType, final Square from, final Square to, final PieceType captured) {
		super(playerType, from, to);
		this.captured = captured;
	}

	public PieceType getCaptured() {
		return captured;
	}

}
