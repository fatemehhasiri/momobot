package net.mauhiz.board.impl.shogi.gui;

import java.awt.Color;
import java.awt.Dimension;

import net.mauhiz.board.impl.common.controller.AbstractPocketInteractiveBoardGui;
import net.mauhiz.board.impl.shogi.ShogiGameController;
import net.mauhiz.board.impl.shogi.data.ShogiBoard;
import net.mauhiz.board.impl.shogi.data.ShogiRule;
import net.mauhiz.board.model.GameController;
import net.mauhiz.board.model.data.Drop;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;
import net.mauhiz.util.PerformanceMonitor;

import org.apache.log4j.Logger;

public class ShogiGui extends AbstractPocketInteractiveBoardGui {

	private static final Logger LOG = Logger.getLogger(ShogiGui.class);

	public static void main(final String... args) {
		final ShogiGui gui = new ShogiGui();
		//		gui.assistant = new ShogiSwtAssistant(gui);
		gui.assistant = new ShogiSwingAssistant(gui);
		LOG.debug("Starting assistant: " + gui.getAssistant());
		gui.getAssistant().start();
	}

	public void afterPromotionDialog(final NormalMove move, final boolean promote) {
		// do not call this.sendMove again here - it would show the promotion dialog again
		if (promote) {
			final Move promotion = getController().convertToPromotion(move);
			super.sendMove(promotion);
		} else {
			super.sendMove(move);
		}
	}

	@Override
	public ShogiBoard getBoard() {
		return (ShogiBoard) super.getBoard();
	}

	@Override
	public ShogiGameController getController() {
		return (ShogiGameController) super.getController();
	}

	@Override
	public Dimension getDefaultSize() {
		return new Dimension(800, 600);
	}

	@Override
	public Dimension getMinimumSize() {
		return getDefaultSize();
	}

	@Override
	public ShogiRule getRule() {
		return (ShogiRule) super.getRule();
	}

	@Override
	public Color getSquareBgcolor(final Square square) {
		return Color.decode("0xFFCC66");
	}

	@Override
	public String getWindowTitle() {
		return "Shogui";
	}

	@Override
	public PlayerType sendMove(final Move move) {
		if (move instanceof NormalMove) {
			final NormalMove nmove = (NormalMove) move;
			if (getRule().canPromote(getBoard(), getSelectedSquare(), nmove.getTo())) {
				getAssistant().showPromotionDialog(nmove);
				return null; // do not send move yet
			}
		}
		return super.sendMove(move);
	}

	@Override
	protected IShogiGuiAssistant getAssistant() {
		return (IShogiGuiAssistant) super.getAssistant();
	}

	@Override
	protected GameController newController() {
		return new ShogiGameController(this);
	}

	@Override
	protected void refreshSquare(final Square square) {
		final PerformanceMonitor sw = new PerformanceMonitor();
		final Piece piece = getBoard().getPieceAt(square);

		if (piece != null && piece.getPlayerType() == getTurn()) {
			addSelectAction(square);
			sw.perfLog("Select action added at " + square, getClass());
			return;
		}

		if (isSquareSelected()) { // from the board
			// available destinations
			final Move move = getRule().generateMove(getSelectedSquare(), square, getGame());

			if (move != null) {
				sw.perfLog("Move generated: " + move, getClass());
				sw.start();
				addMoveAction(square, move);
				sw.perfLog("Move action added at: " + square, getClass());
				return;
			}
		} else if (isPieceSelected()) { // from the pocket
			final Drop drop = getRule().generateMove(selectedPiece, square, getGame());

			if (drop != null) {
				sw.perfLog("Drop generated: " + drop, getClass());
				sw.start();
				addMoveAction(square, drop);
				sw.perfLog("Drop action added at " + square, getClass());
				return;
			}
		}
		// no action : disable button
		disableSquare(square);
	}
}
