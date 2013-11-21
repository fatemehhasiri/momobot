package net.mauhiz.board.impl.common.gui;

import java.awt.Dimension;

import net.mauhiz.board.impl.common.action.CancelAction;
import net.mauhiz.board.impl.common.action.MoveAction;
import net.mauhiz.board.impl.common.action.SelectSquareAction;
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

	@Override
	public void addCancelAction(final Square square) {
		LOG.trace("Adding cancel action to square: " + square);
		enableSquare(square, CancelAction.getInstance(this));
	}

	@Override
	public void addMoveAction(final Square square, final Move move) {
		LOG.trace("Adding move action to square: " + square);
		enableSquare(square, new MoveAction(this, move));
	}

	@Override
	public void addSelectAction(final Square square) {
		LOG.trace("Adding select action to square: " + square);
		enableSquare(square, new SelectSquareAction(this, square));
	}

	@Override
	public void cancelSelection() {
		LOG.trace("Cancelling selection: " + selectedSquare);
		selectSquare(null);
	}

	@Override
	public void close() {
		LOG.info("Closing GUI");
		getAssistant().close();
	}

	@Override
	public void disableSquare(final Square square) {
		getAssistant().disableSquare(square);
	}

	public void enableSquare(final Square square, final IAction action) {
		getAssistant().enableSquare(square, action);
	}

	@Override
	public Dimension getDefaultSize() {
		return new Dimension(400, 400);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(0, 0);
	}

	@Override
	public Square getSelectedSquare() {
		return selectedSquare;
	}

	public PlayerType getTurn() {
		return getGame().getTurn();
	}

	@Override
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
			public ExecutionType getExecutionType() {
				return ExecutionType.PARALLEL_CACHED;
			}

			@Override
			public void mrun() {
				final Board board = getBoard();
				for (final Square square : board.getSquares()) {
					refreshSquare(square);
				}
				if (isSquareSelected()) {
					addCancelAction(selectedSquare);
				}
			}
		}.launch();
	}

	public void refreshDecorations() {
		new MonitoredRunnable("Refresh Decorations", "Refreshed decorations") {
			@Override
			public ExecutionType getExecutionType() {
				return ExecutionType.PARALLEL_CACHED;
			}

			@Override
			public void mrun() {
				final Board board = getBoard();
				for (final Square square : board.getSquares()) {
					assistant.decorate(square, board.getPieceAt(square));
				}
			}
		}.launch();
	}

	@Override
	public void selectSquare(final Square at) {
		selectedSquare = at;
		refreshActions();
	}

	@Override
	public PlayerType sendMove(final Move move) {
		final PlayerType player = getController().receiveMove(move);
		cancelSelection();
		refreshDecorations();
		return player;
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

	protected Game getGame() {
		return getController().getGame();
	}

	protected Rule getRule() {
		return getGame().getRule();
	}

	protected boolean isSquareSelected() {
		return getSelectedSquare() != null;
	}

	protected abstract GameController newController();

	protected abstract void refreshSquare(Square square);

	private void appendSquares(final GuiAssistant pAssistant, final Dimension size) {
		pAssistant.appendSquares(getBoard().getSquares(), size);

	}
}
