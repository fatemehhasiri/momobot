package net.mauhiz.board.impl.common.gui;

import net.mauhiz.board.model.gui.BoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;

/**
 * @author mauhiz
 */
public class StartAction extends AbstractAction {

	private final BoardGui gui;

	public StartAction(BoardGui gui) {
		super();
		this.gui = gui;
	}

	@Override
	protected ExecutionType getExecutionType() {
		return ExecutionType.PARALLEL_CACHED;
	}

	@Override
	public void trun() {
		gui.newGame();
	}
}
