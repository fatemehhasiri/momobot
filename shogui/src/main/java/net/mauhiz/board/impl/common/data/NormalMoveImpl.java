package net.mauhiz.board.impl.common.data;

import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;

public class NormalMoveImpl extends AbstractMove implements NormalMove {
	private final Square from;
	private final Square to;

	public NormalMoveImpl(final PlayerType playerType, final Square from, final Square to) {
		super(playerType);
		this.from = from;
		this.to = to;
	}

	@Override
	public Square getFrom() {
		return from;
	}

	@Override
	public Square getTo() {
		return to;
	}

	@Override
	public String toString() {
		return from + "->" + to;
	}

}
