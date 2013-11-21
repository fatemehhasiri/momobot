package net.mauhiz.board.impl.checkers.data;

import net.mauhiz.board.model.data.PieceType;

public enum CheckersPieceType implements PieceType {
	PAWN("O"), QUEEN("8");

	private String name;

	private CheckersPieceType(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}
}
