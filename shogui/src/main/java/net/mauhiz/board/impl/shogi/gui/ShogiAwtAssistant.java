package net.mauhiz.board.impl.shogi.gui;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JPanel;

import net.mauhiz.board.impl.common.assistant.applet.PocketAwtGuiAssistant;
import net.mauhiz.board.impl.common.assistant.swing.button.RotatingJButton;
import net.mauhiz.board.impl.shogi.data.ShogiPieceType;
import net.mauhiz.board.impl.shogi.data.ShogiPlayerType;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.PocketBoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;

/**
 * @author mauhiz
 */
public class ShogiAwtAssistant extends PocketAwtGuiAssistant implements IShogiGuiAssistant {

	class PromotionAcceptedAction extends AbstractAction {
		private final NormalMove move;
		private final Dialog popup;

		PromotionAcceptedAction(final Dialog popup, final NormalMove move) {
			super();
			this.popup = popup;
			this.move = move;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		public void trun() {
			getParent().afterPromotionDialog(move, true);
			popup.dispose();
		}
	}

	class PromotionRefusedAction extends AbstractAction {
		private final NormalMove move;
		private final Dialog popup;

		PromotionRefusedAction(final Dialog popup, final NormalMove move) {
			super();
			this.popup = popup;
			this.move = move;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		public void trun() {
			getParent().afterPromotionDialog(move, false);
			popup.dispose();
		}
	}

	public ShogiAwtAssistant(final PocketBoardGui parent) {
		super(parent);
	}

	@Override
	public void decorate(final RotatingJButton button, final PieceType piece, final PlayerType player) {
		if (piece == null) {
			button.setText("", false);
		} else {
			String ji;
			if (piece == ShogiPieceType.KING && player == ShogiPlayerType.SENTE) {
				ji = "玉"; // special case!
			} else {
				ji = piece.toString();
			}
			button.setText(ji, player == ShogiPlayerType.GOTE);
		}
	}

	@Override
	public void initPockets() {
		JPanel pocket = new JPanel();
		frame.add(pocket, 0);
		pockets.put(ShogiPlayerType.SENTE, pocket);
		pocket = new JPanel();
		frame.add(pocket);
		pockets.put(ShogiPlayerType.GOTE, pocket);
	}

	@Override
	public void showPromotionDialog(final NormalMove move) {
		final Dialog popup = new Dialog(Frame.getFrames()[0], "Promotion?");
		popup.setLayout(new GridLayout(1, 2, 10, 10));
		popup.setModal(true);

		final Button promoButton = new Button("Yes");
		promoButton.addActionListener(new PromotionAcceptedAction(popup, move));
		popup.add(promoButton);

		final Button cancelButton = new Button("No");
		cancelButton.addActionListener(new PromotionRefusedAction(popup, move));
		popup.add(cancelButton);

		popup.pack();
		popup.setVisible(true);
	}

	@Override
	protected ShogiGui getParent() {
		return (ShogiGui) super.getParent();
	}
}
