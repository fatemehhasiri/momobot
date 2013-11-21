package net.mauhiz.board.impl.shogi.gui;

import java.util.Map;

import net.mauhiz.board.impl.common.assistant.jface.PocketSwtGuiAssistant;
import net.mauhiz.board.impl.shogi.data.ShogiPieceType;
import net.mauhiz.board.impl.shogi.data.ShogiPlayerType;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.PocketBoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;

public class ShogiSwtAssistant extends PocketSwtGuiAssistant implements IShogiGuiAssistant {
	static class ButtonDecorator extends AbstractAction {
		private final Button button;
		private final PieceType piece;
		private final PlayerType player;

		ButtonDecorator(final Button button, final PieceType piece, final PlayerType player) {
			super();
			this.button = button;
			this.piece = piece;
			this.player = player;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_SYNCHRONOUS;
		}

		@Override
		public void trun() {
			// FIXME disabled buttons ignore setText command
			if (piece == null) {
				button.setText("");
			} else {
				final ShogiPieceType pt = (ShogiPieceType) piece;
				if (player == ShogiPlayerType.SENTE) {
					button.setText("^\r\n" + pt.getName());
				} else {
					button.setText(pt.getName() + "\r\nV");
				}
			}
		}
	}

	class PocketInitializer extends AbstractAction {
		private final Map<PlayerType, Composite> lpockets;

		PocketInitializer(final Map<PlayerType, Composite> lpockets) {
			super();
			this.lpockets = lpockets;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		protected void trun() {
			lpockets.put(ShogiPlayerType.GOTE, new Group(getShell(), SWT.TOP));
			lpockets.put(ShogiPlayerType.SENTE, new Group(getShell(), SWT.BOTTOM));
		}
	}

	public ShogiSwtAssistant(final PocketBoardGui parent) {
		super(parent);
	}

	@Override
	public void decorate(final Button button, final PieceType piece, final PlayerType player) {
		new ButtonDecorator(button, piece, player).launch(button.getDisplay());
	}

	@Override
	public ShogiGui getParent() {
		return (ShogiGui) super.getParent();
	}

	@Override
	public void initPockets() {
		new PocketInitializer(pockets).launch(getShell().getDisplay());
	}

	@Override
	public void showPromotionDialog(final NormalMove move) {
		final MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setMessage("Promote?");
		final int buttonID = mb.open();
		switch (buttonID) {
			case SWT.YES:
				getParent().afterPromotionDialog(move, true);
				break;
			default:
				getParent().afterPromotionDialog(move, false);
		}
	}
}
