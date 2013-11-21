package net.mauhiz.board.impl.common.data;

import java.awt.Dimension;
import java.util.Iterator;

import net.mauhiz.board.model.data.Square;

public class SquareView implements Iterable<Square>, Iterator<Square> {

	private final Dimension boardSize;
	private int counter;

	public SquareView(final Dimension boardSize) {
		super();
		this.boardSize = boardSize;
	}

	@Override
	public boolean hasNext() {
		return counter < getNumSquares();
	}

	@Override
	public Iterator<Square> iterator() {
		counter = 0;
		return this;
	}

	@Override
	public Square next() {
		final int x = counter % boardSize.width;
		final int y = (counter - x) / boardSize.width;
		counter++;
		return SquareImpl.getInstance(x, y);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private int getNumSquares() {
		return boardSize.height * boardSize.width;
	}
}
