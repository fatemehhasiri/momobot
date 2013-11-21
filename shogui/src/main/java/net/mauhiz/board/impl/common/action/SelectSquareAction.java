package net.mauhiz.board.impl.common.action;

import net.mauhiz.board.model.data.Square;
import net.mauhiz.board.model.gui.InteractiveBoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;

/**
 * @author mauhiz
 */
public class SelectSquareAction extends AbstractAction {

	private final Square at;
	private final InteractiveBoardGui gui;

	public SelectSquareAction(final InteractiveBoardGui gui, final Square at) {
		super();
		this.gui = gui;
		this.at = at;
	}

	@Override
	public boolean equals(final Object obj) {
		return this == obj || obj instanceof SelectSquareAction && isEquals((SelectSquareAction) obj);
	}

	@Override
	public ExecutionType getExecutionType() {
		return ExecutionType.PARALLEL_CACHED;
	}

	@Override
	public int hashCode() {
		return at.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + at;
	}

	@Override
	public void trun() {
		gui.selectSquare(at);
	}

	private boolean isEquals(final SelectSquareAction other) {
		return at.equals(other.at) && gui == other.gui;
	}
}
