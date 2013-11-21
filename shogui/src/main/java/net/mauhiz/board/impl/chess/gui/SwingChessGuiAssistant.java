package net.mauhiz.board.impl.chess.gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;

import net.mauhiz.board.impl.chess.data.ChessPieceType;
import net.mauhiz.board.impl.chess.data.ChessPlayerType;
import net.mauhiz.board.impl.common.assistant.swing.SwingGuiAssistant;
import net.mauhiz.board.impl.common.assistant.swing.button.RotatingJButton;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.BoardGui;

/**
 * @author mauhiz
 */
public class SwingChessGuiAssistant extends SwingGuiAssistant implements IChessGuiAssistant {

	public SwingChessGuiAssistant(final BoardGui parent) {
		super(parent);
	}

	@Override
	public void decorate(final RotatingJButton button, final PieceType op, final PlayerType player) {
		if (op == null) {
			button.setText("");
		} else {
			button.setText(op.toString());
			button.setForeground(player == ChessPlayerType.BLACK ? Color.BLACK : Color.WHITE);
		}
	}

	@Override
	public void showPromotionDialog(final ChessPieceType[] promotions, final NormalMove nmove) {
		final JDialog popup = new JDialog(frame, "Promotion?");
		popup.setLayout(new GridLayout(1, promotions.length));
		popup.setModal(true);

		for (final ChessPieceType promotion : promotions) {
			final JButton promoButton = new JButton(promotion.getName());
			promoButton.addActionListener(new PromoteAction(nmove, getParent(), promotion));
			popup.add(promoButton);
		}
		popup.pack();
		popup.setVisible(true);
	}
}
