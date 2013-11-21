package net.mauhiz.board.impl.checkers.gui;

import java.awt.Color;

import net.mauhiz.board.impl.checkers.data.CheckersPlayerType;
import net.mauhiz.board.impl.common.assistant.swing.SwingGuiAssistant;
import net.mauhiz.board.impl.common.assistant.swing.button.RotatingJButton;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.BoardGui;

public class CheckersSwingAssistant extends SwingGuiAssistant {

	public CheckersSwingAssistant(final BoardGui parent) {
		super(parent);
	}

	@Override
	protected void decorate(final RotatingJButton button, final PieceType piece, final PlayerType player) {
		if (piece == null) {
			button.setText("");
		} else {
			button.setText(piece.toString());
			button.setForeground(player == CheckersPlayerType.BLACK ? Color.BLACK : Color.WHITE);
		}
	}

}
