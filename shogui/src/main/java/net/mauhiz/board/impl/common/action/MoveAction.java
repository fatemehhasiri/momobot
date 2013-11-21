package net.mauhiz.board.impl.common.action;

import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.gui.InteractiveBoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;

/**
 * @author mauhiz
 */
public class MoveAction extends AbstractAction {

	private final InteractiveBoardGui gui;
	private final Move move;

	public MoveAction(final InteractiveBoardGui gui, final Move move) {
		super();
		this.move = move;
		this.gui = gui;
	}

	@Override
	public ExecutionType getExecutionType() {
		return ExecutionType.PARALLEL_CACHED;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " with move: " + move;
	}

	@Override
	public void trun() {
		gui.sendMove(move);
	}
}
