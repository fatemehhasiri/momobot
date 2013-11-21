package net.mauhiz.board.impl.common.action;

import net.mauhiz.board.model.gui.BoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;
import net.mauhiz.util.ThreadManager;

/**
 * @author mauhiz
 */
public class ExitAction extends AbstractAction {
	private final BoardGui gui;

	public ExitAction(final BoardGui gui) {
		super();
		this.gui = gui;
	}

	@Override
	public ExecutionType getExecutionType() {
		return ExecutionType.GUI_SYNCHRONOUS;
	}

	@Override
	public void trun() {
		gui.close();
		ThreadManager.shutdown();
	}
}
