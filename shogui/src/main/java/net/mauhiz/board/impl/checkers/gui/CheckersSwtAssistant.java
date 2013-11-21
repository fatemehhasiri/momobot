package net.mauhiz.board.impl.checkers.gui;

import net.mauhiz.board.impl.checkers.data.CheckersPieceType;
import net.mauhiz.board.impl.checkers.data.CheckersPlayerType;
import net.mauhiz.board.impl.common.assistant.jface.SwtGuiAssistant;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.BoardGui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;

public class CheckersSwtAssistant extends SwtGuiAssistant {

	public CheckersSwtAssistant(final BoardGui parent) {
		super(parent);
	}

	@Override
	protected void decorate(final Button button, final PieceType piece, final PlayerType player) {
		if (piece == null) {
			button.setText("");
		} else {
			final CheckersPieceType op = (CheckersPieceType) piece;
			button.setText(op.getName());

			final Display display = button.getDisplay();
			final Color black = display.getSystemColor(SWT.COLOR_BLACK);
			final Color white = display.getSystemColor(SWT.COLOR_WHITE);
			button.setForeground(player == CheckersPlayerType.BLACK ? black : white);
		}
	}

}
