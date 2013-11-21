package net.mauhiz.board.impl.chess.gui;

import net.mauhiz.board.impl.chess.PromoteMove;
import net.mauhiz.board.impl.chess.data.ChessPieceType;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.gui.BoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;

public class PromoteAction extends AbstractAction {
	private final BoardGui lparent;
	private final NormalMove nmove;
	private final ChessPieceType promotion;

	public PromoteAction(final NormalMove nmove, final BoardGui lparent, final ChessPieceType promotion) {
		super();
		this.nmove = nmove;
		this.lparent = lparent;
		this.promotion = promotion;
	}

	@Override
	public ExecutionType getExecutionType() {
		return ExecutionType.GUI_SYNCHRONOUS;
	}

	@Override
	public void trun() {
		final Move promoteMove = new PromoteMove(nmove, promotion);
		lparent.sendMove(promoteMove);
	}
}