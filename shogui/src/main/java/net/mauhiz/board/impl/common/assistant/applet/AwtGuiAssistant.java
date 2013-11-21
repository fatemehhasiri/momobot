package net.mauhiz.board.impl.common.assistant.applet;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.mauhiz.board.impl.common.action.ExitAction;
import net.mauhiz.board.impl.common.action.StartAction;
import net.mauhiz.board.impl.common.assistant.AbstractGuiAssistant;
import net.mauhiz.board.impl.common.assistant.swing.button.RotatingJButton;
import net.mauhiz.board.impl.common.data.SquareImpl;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;
import net.mauhiz.board.model.gui.BoardGui;
import net.mauhiz.util.IAction;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

public abstract class AwtGuiAssistant extends AbstractGuiAssistant {
	private static final Logger LOG = Logger.getLogger(AwtGuiAssistant.class);
	protected final JApplet frame = new JApplet();
	protected final JPanel panel = new JPanel();
	private final Map<Square, RotatingJButton> buttons = new HashMap<>();

	public AwtGuiAssistant(final BoardGui parent) {
		super(parent);
	}

	@Override
	public void addToHistory(final String value, final IAction action) {
		throw new NotImplementedException();
	}

	@Override
	public void appendSquares(final Iterable<Square> squares, final Dimension size) {
		panel.setVisible(false);
		for (final Square square : squares) {
			final int x = square.getX();
			final int y = size.height - square.getY() - 1;
			final RotatingJButton button = new RotatingJButton();
			buttons.put(SquareImpl.getInstance(x, y), button);
			button.setBackground(getParent().getSquareBgcolor(square));
			button.setSize(30, 30);
			panel.add(button);
		}
		panel.setVisible(true);
	}

	@Override
	public void clear() {
		for (final RotatingJButton button : buttons.values()) {
			panel.remove(button);
		}
		buttons.clear();
		clearListeners();
	}

	@Override
	public void close() {
		frame.destroy();
	}

	@Override
	public void decorate(final Square square, final Piece piece) {
		final RotatingJButton button = getButton(square);
		if (piece == null) {
			decorate(button, null, null);
		} else {
			decorate(button, piece.getPieceType(), piece.getPlayerType());
		}
	}

	@Override
	public void disableSquare(final Square square) {
		final ActionListener action = removeListener(square);
		if (action != null) {
			final RotatingJButton button = getButton(square);
			button.removeActionListener(action);
			button.setEnabled(false);
		}
	}

	@Override
	public void enableSquare(final Square square, final IAction action) {
		final IAction former = putListener(square, action);
		if (ObjectUtils.equals(former, action)) {
			return;
		}

		final RotatingJButton button = getButton(square);
		if (former == null) {
			LOG.debug("Square: " + square + ", enabled with action: " + action);
			button.addActionListener(action);
			button.setEnabled(true);
		} else {
			LOG.debug("Square: " + square + ", action: " + former + " with action: " + action);
			button.removeActionListener(former);
			button.addActionListener(action);
		}
	}

	public RotatingJButton getButton(final Square at) {
		return buttons.get(at);
	}

	@Override
	public void initDisplay() {
		initMenu();
		frame.setName(getParent().getWindowTitle());

		final Dimension defaultSize = getParent().getDefaultSize();
		frame.setSize(defaultSize);

		final Dimension minSize = getParent().getMinimumSize();
		frame.setMinimumSize(minSize);

		frame.add(panel);
		frame.setVisible(true);
	}

	@Override
	public void initLayout(final Dimension size) {

		/* layout */
		final GridLayout gridLayout = new GridLayout(size.width, size.height, 0, 0);
		panel.setLayout(gridLayout);
	}

	@Override
	public void refreshBoard() {
		panel.validate();
	}

	@Override
	public void start() {
		initDisplay();
	}

	protected abstract void decorate(RotatingJButton button, PieceType piece, PlayerType player);

	protected void initMenu() {

		/* menu */
		final Container menuBar = new Container();
		menuBar.setLayout(new GridLayout(1, 2));

		final JButton fileStartItem = new JButton("New Game");
		fileStartItem.addActionListener(new StartAction(getParent()));
		menuBar.add(fileStartItem);

		final JButton fileExitItem = new JButton("Exit");
		fileExitItem.addActionListener(new ExitAction(getParent()));
		menuBar.add(fileExitItem);
		frame.add(menuBar);
	}
}
