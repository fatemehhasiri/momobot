package net.mauhiz.board.impl.common.action;

import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.PocketBoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;

public class SelectPocketAction extends AbstractAction {

	private final PocketBoardGui gui;
	private final PieceType piece;
	private final PlayerType playerType;

	public SelectPocketAction(final PocketBoardGui gui, final PieceType piece, final PlayerType playerType) {
		super();
		this.gui = gui;
		this.piece = piece;
		this.playerType = playerType;
	}

	@Override
	public ExecutionType getExecutionType() {
		return ExecutionType.GUI_SYNCHRONOUS;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " for player: " + playerType + " with piece: " + piece;
	}

	@Override
	public void trun() {
		gui.selectPieceToDrop(playerType, piece);
	}
}
