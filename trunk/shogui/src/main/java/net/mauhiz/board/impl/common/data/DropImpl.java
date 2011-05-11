package net.mauhiz.board.impl.common.data;

import net.mauhiz.board.model.data.Drop;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;

public class DropImpl extends AbstractMove implements Drop {
	private PieceType pieceType;

	private Square to;
	public DropImpl(PlayerType playerType, PieceType pieceType, Square to) {
		super(playerType);
		this.to = to;
		this.pieceType = pieceType;
	}

	@Override
	public PieceType getPieceType() {
		return pieceType;
	}

	@Override
	public Square getTo() {
		return to;
	}
}
