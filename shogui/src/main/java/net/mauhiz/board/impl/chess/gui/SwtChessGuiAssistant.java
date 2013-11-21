package net.mauhiz.board.impl.chess.gui;

import net.mauhiz.board.impl.chess.data.ChessPieceType;
import net.mauhiz.board.impl.chess.data.ChessPlayerType;
import net.mauhiz.board.impl.common.assistant.jface.SwtGuiAssistant;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.BoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SwtChessGuiAssistant extends SwtGuiAssistant implements IChessGuiAssistant {
	static class ButtonDecorator extends AbstractAction {
		private final Button button;
		private final PieceType piece;
		private final PlayerType player;

		ButtonDecorator(final Button button, final PieceType piece, final PlayerType player) {
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
			if (piece == null) {
				button.setText("");
			} else {
				button.setText(piece.toString());
				final Display display = button.getDisplay();
				final Color black = display.getSystemColor(SWT.COLOR_BLACK);
				final Color white = display.getSystemColor(SWT.COLOR_WHITE);
				button.setForeground(player == ChessPlayerType.BLACK ? black : white);
			}
		}
	}

	public SwtChessGuiAssistant(final BoardGui parent) {
		super(parent);
	}

	@Override
	public void decorate(final Button button, final PieceType piece, final PlayerType player) {
		new ButtonDecorator(button, piece, player).launch(button.getDisplay());
	}

	public boolean showPromotionDialog() {
		final MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setMessage("Promote?");
		final int buttonID = mb.open();
		switch (buttonID) {
			case SWT.YES:
				return true;
			default:
				return false;
		}
	}

	@Override
	public void showPromotionDialog(final ChessPieceType[] promotions, final NormalMove nmove) {
		final Shell popup = new Shell(getShell(), SWT.ICON_QUESTION);
		popup.setLayout(new GridLayout(promotions.length, true));
		popup.setText("Promotion?");
		popup.setActive();

		for (final ChessPieceType promotion : promotions) {
			final Button promoButton = new Button(popup, SWT.PUSH);
			promoButton.setText(promotion.getName());
			promoButton.setToolTipText(promotion.name());
			promoButton.addSelectionListener(new PromoteAction(nmove, getParent(), promotion));
		}
		popup.pack();
		popup.open();
	}
}
