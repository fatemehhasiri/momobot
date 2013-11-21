package net.mauhiz.board.impl.chess;

import net.mauhiz.board.impl.common.data.NormalMoveImpl;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;

public class EnPassant extends NormalMoveImpl {

	public EnPassant(final PlayerType playerType, final Square from, final Square to) {
		super(playerType, from, to);
	}

}
