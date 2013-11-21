package net.mauhiz.board.impl.go.gui;

import java.awt.Color;

import net.mauhiz.board.impl.common.controller.AbstractPocketInteractiveBoardGui;
import net.mauhiz.board.impl.go.GoGameController;
import net.mauhiz.board.impl.go.data.GoBoard;
import net.mauhiz.board.impl.go.data.GoRule;
import net.mauhiz.board.model.GameController;
import net.mauhiz.board.model.data.Drop;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.Square;

public class GoGui extends AbstractPocketInteractiveBoardGui {

	public static void main(final String... args) {
		final GoGui gui = new GoGui();
		// gui.assistant = new ShogiSwtAssistant(gui);
		gui.assistant = new GoSwingAssistant(gui);
		gui.getAssistant().start();
	}

	@Override
	public GoBoard getBoard() {
		return (GoBoard) super.getBoard();
	}

	@Override
	public GoGameController getController() {
		return (GoGameController) super.getController();
	}

	@Override
	public GoRule getRule() {
		return (GoRule) super.getRule();
	}

	@Override
	public Color getSquareBgcolor(final Square square) {
		return Color.decode("0xFFCC66");
	}

	@Override
	public String getWindowTitle() {
		return "Go GUI";
	}

	@Override
	protected GameController newController() {
		return new GoGameController(this);
	}

	@Override
	protected void refreshSquare(final Square square) {
		final Piece piece = getBoard().getPieceAt(square);
		if (isPieceSelected()) { // from the pocket
			final Drop drop = getRule().generateMove(selectedPiece, square, getGame());
			if (drop != null) {
				addMoveAction(square, drop);
				return;
			}
		} else if (piece != null && piece.getPlayerType() == getTurn()) { // available pieces
			addSelectAction(square);
			return;
		}

		disableSquare(square);
	}
}
