package net.mauhiz.board.impl.common.gui;

import java.awt.Dimension;

import net.mauhiz.board.model.GameController;
import net.mauhiz.board.model.data.Board;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Rule;
import net.mauhiz.board.model.data.Square;
import net.mauhiz.board.model.gui.GuiAssistant;
import net.mauhiz.board.model.gui.InteractiveBoardGui;
import net.mauhiz.util.ExecutionType;
import net.mauhiz.util.IAction;
import net.mauhiz.util.MonitoredRunnable;

import org.apache.log4j.Logger;

public abstract class AbstractInteractiveBoardGui implements InteractiveBoardGui {
	private static final Logger LOG = Logger.getLogger(AbstractInteractiveBoardGui.class);
	protected GuiAssistant assistant;
	protected GameController controller;
	protected Square selectedSquare;

	public void addCancelAction(Square square) {
		LOG.trace("Adding cancel action to square: " + square);
		enableSquare(square, CancelAction.getInstance(this));
	}

	public void addMoveAction(Square square, Move move) {
		LOG.trace("Adding move action to square: " + square);
		enableSquare(square, new MoveAction(this, move));
	}

	public void addSelectAction(Square square) {
		LOG.trace("Adding select action to square: " + square);
		enableSquare(square, new SelectSquareAction(this, square));
	}

	private void appendSquares(final GuiAssistant pAssistant, final Dimension size) {
		pAssistant.appendSquares(getBoard().getSquares(), size);

	}

	public void cancelSelection() {
		LOG.trace("Cancelling selection: " + selectedSquare);
		selectSquare(null);
	}

	public void close() {
		LOG.info("Closing GUI");
		getAssistant().close();
	}

	public void disableSquare(Square square) {
		getAssistant().disableSquare(square);
	}

	public void enableSquare(Square square, IAction action) {
		getAssistant().enableSquare(square, action);
	}

	protected GuiAssistant getAssistant() {
		return assistant;
	}

	protected Board getBoard() {
		return getGame().getLastBoard();
	}

	protected GameController getController() {
		return controller;
	}

	public Dimension getDefaultSize() {
		return new Dimension(400, 400);
	}

	protected Game getGame() {
		return getController().getGame();
	}

	public Dimension getMinimumSize() {
		return new Dimension(0, 0);
	}

	protected Rule getRule() {
		return getGame().getRule();
	}

	public Square getSelectedSquare() {
		return selectedSquare;
	}

	public PlayerType getTurn() {
		return getGame().getTurn();
	}

	protected boolean isSquareSelected() {
		return getSelectedSquare() != null;
	}

	protected abstract GameController newController();

	public void newGame() {
		LOG.info("Starting new game");
		controller = newController();
		final Dimension size = getBoard().getSize();
		getAssistant().clear();
		getAssistant().initLayout(size);
		appendSquares(getAssistant(), size);

		refreshDecorations();
		refreshActions();
	}

	public void refreshActions() {
		new MonitoredRunnable("Refresh Actions", "Refreshed actions") {
			@Override
			protected ExecutionType getExecutionType() {
				return ExecutionType.PARALLEL_CACHED;
			}

			@Override
			public void mrun() {
				Board board = getBoard();
				for (Square square : board.getSquares()) {
					refreshSquare(square);
				}
				if (isSquareSelected()) {
					addCancelAction(selectedSquare);
				}
			}
		}.launch(null);
	}

	public void refreshDecorations() {
		new MonitoredRunnable("Refresh Decorations", "Refreshed decorations") {
			@Override
			protected ExecutionType getExecutionType() {
				return ExecutionType.PARALLEL_CACHED;
			}

			@Override
			public void mrun() {
				Board board = getBoard();
				for (Square square : board.getSquares()) {
					assistant.decorate(square, board.getPieceAt(square));
				}
			}
		}.launch(null);
	}

	protected abstract void refreshSquare(Square square);

	public void selectSquare(Square at) {
		selectedSquare = at;
		refreshActions();
	}

	public PlayerType sendMove(Move move) {
		PlayerType player = getController().receiveMove(move);
		cancelSelection();
		refreshDecorations();
		return player;
	}
}
