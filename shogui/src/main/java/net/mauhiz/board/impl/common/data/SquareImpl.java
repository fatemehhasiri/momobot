package net.mauhiz.board.impl.common.data;

import net.mauhiz.board.model.data.Square;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SquareImpl implements Square {
	private static final int _CACHE_HEIGHT = 20;
	private static final int _CACHE_WIDTH = 20;
	private static final Square[][] CACHE = new Square[_CACHE_WIDTH][_CACHE_HEIGHT];

	public static Square getInstance(final int x, final int y) {
		final boolean inRange = x >= 0 && x < _CACHE_WIDTH && y >= 0 && y < _CACHE_HEIGHT;
		if (inRange) {
			Square square = CACHE[x][y];
			if (square == null) {
				square = new SquareImpl(x, y);
				CACHE[x][y] = square;
			}
			return square;
		}

		return new SquareImpl(x, y);
	}

	public final int x;
	public final int y;

	public SquareImpl(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(final Square other) {
		return new CompareToBuilder().append(getX(), other.getX()).append(getY(), other.getY()).toComparison();
	}

	@Override
	public boolean equals(final Object obj) {
		return this == obj || obj instanceof Square && isEquals((Square) obj);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(x).append(y).toHashCode();
	}

	@Override
	public String toString() {
		// indices are 0-based, so add a 1 offset to display
		return "(" + (x + 1) + "," + (y + 1) + ")";
	}

	private boolean isEquals(final Square other) {
		return other.getX() == x && other.getY() == y;
	}
}
