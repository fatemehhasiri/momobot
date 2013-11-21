package net.mauhiz.board.impl.common.assistant.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

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
import net.mauhiz.board.remote.NewRemoteGameAction;
import net.mauhiz.util.AbstractNamedRunnable;
import net.mauhiz.util.ExecutionType;
import net.mauhiz.util.IAction;
import net.mauhiz.util.PerformanceMonitor;
import net.mauhiz.util.ThreadUtils;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

public abstract class SwingGuiAssistant extends AbstractGuiAssistant {
	final class ButtonInitializer extends AbstractNamedRunnable {
		private final Color bgColor;
		private final RotatingJButton button;

		ButtonInitializer(final Color bgColor, final RotatingJButton button) {
			super("Button initializer");
			this.bgColor = bgColor;
			this.button = button;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_SYNCHRONOUS;
		}

		@Override
		protected void trun() {
			button.setBackground(bgColor);
			button.setSize(30, 30);
			button.setEnabled(false);
			boardPanel.add(button);
		}
	}

	private static final Logger LOG = Logger.getLogger(SwingGuiAssistant.class);
	protected final JSplitPane boardAndHistory = new JSplitPane();
	protected final JPanel boardPanel = new JPanel();
	protected final JFrame frame = new JFrame();
	protected final JList<String> historyPanel = new JList<>(new DefaultListModel<String>());
	private final Map<Square, RotatingJButton> buttons = new HashMap<>();

	public SwingGuiAssistant(final BoardGui parent) {
		super(parent);
	}

	@Override
	public void addToHistory(final String value, final IAction action) {
		final DefaultListModel<String> dlm = (DefaultListModel<String>) historyPanel.getModel();
		dlm.addElement(value);
	}

	@Override
	public void appendSquares(final Iterable<Square> squares, final Dimension size) {
		boardPanel.setVisible(false);
		for (final Square square : squares) {
			final Square cartesianSquare = SquareImpl.getInstance(square.getX(), size.height - square.getY() - 1);
			final RotatingJButton button = new RotatingJButton();
			new ButtonInitializer(getParent().getSquareBgcolor(square), button).launch();
			synchronized (buttons) {
				buttons.put(cartesianSquare, button);
			}
		}
		boardPanel.setVisible(true);
	}

	@Override
	public void clear() {
		if (boardPanel.getComponentCount() > 0) {
			boardPanel.removeAll();
			LOG.debug("Board panel cleared");
		}
		synchronized (historyPanel) {
			final DefaultListModel<String> model = (DefaultListModel<String>) historyPanel.getModel();
			model.clear();
			if (historyPanel.getComponentCount() > 0) {
				historyPanel.removeAll();
				LOG.debug("History panel cleared");
			}

		}
		synchronized (buttons) {
			buttons.clear();
		}

		clearListeners();
	}

	@Override
	public void close() {
		frame.dispose();
		LOG.info("Display disposed");
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
		}
	}

	@Override
	public void enableSquare(final Square square, final IAction action) {
		final IAction former = putListener(square, action);
		if (ObjectUtils.equals(former, action)) {
			return;
		}

		final RotatingJButton button = getButton(square);

		// the order is important to not trigger a disable/enable flip
		button.addActionListener(action);

		if (former != null) {
			button.removeActionListener(former);
		}
	}

	public RotatingJButton getButton(final Square at) {
		// This loop waits until the buttons have actually been added.
		while (true) {
			RotatingJButton button;
			synchronized (buttons) {
				button = buttons.get(at);
			}
			if (button == null) {
				// not ready yet
				ThreadUtils.safeSleep(100);
			} else {
				return button;
			}
		}
	}

	@Override
	public void initDisplay() {
		initMenu();
		initPanels();

		frame.setTitle(getParent().getWindowTitle());

		final Dimension defaultSize = getParent().getDefaultSize();
		frame.setSize(defaultSize);

		final Dimension minSize = getParent().getMinimumSize();
		frame.setMinimumSize(minSize);
		frame.setVisible(true);
	}

	@Override
	public void initLayout(final Dimension size) {
		final PerformanceMonitor sw = new PerformanceMonitor();
		final GridLayout gridLayout = new GridLayout(size.width, size.height, 0, 0);
		boardPanel.setLayout(gridLayout);
		sw.perfLog("Board layout initialized: " + boardPanel.getLayout(), getClass());
	}

	@Override
	public void refreshBoard() {
		final PerformanceMonitor sw = new PerformanceMonitor();
		boardAndHistory.validate();
		sw.perfLog("Repainted: " + boardAndHistory, getClass());
	}

	@Override
	public void start() {
		initDisplay();
	}

	protected abstract void decorate(RotatingJButton button, PieceType piece, PlayerType player);

	protected void initMenu() {
		final PerformanceMonitor sw = new PerformanceMonitor();
		/* menu */
		final JMenuBar menuBar = new JMenuBar();
		final JMenu fileMenu = new JMenu("File");

		final JMenuItem fileStartItem = new JMenuItem("New Game");
		fileMenu.add(fileStartItem);
		fileStartItem.addActionListener(new StartAction(getParent()));

		final JMenuItem fileRemoteItem = new JMenuItem("New &Network Game");
		fileMenu.add(fileRemoteItem);
		fileRemoteItem.addActionListener(new NewRemoteGameAction(getParent()));

		final JMenuItem fileExitItem = new JMenuItem("Exit");
		fileMenu.add(fileExitItem);
		fileExitItem.addActionListener(new ExitAction(getParent()));

		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
		sw.perfLog("Menu ready", getClass());
	}

	protected void initPanels() {
		boardAndHistory.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		boardAndHistory.setSize(frame.getWidth() - 2, frame.getHeight() - 2);
		frame.add(boardAndHistory);
		LOG.debug("Init board and history split panel: " + boardAndHistory);

		boardPanel.setSize(boardAndHistory.getWidth() / 2 - 2, boardAndHistory.getHeight());
		boardAndHistory.setLeftComponent(boardPanel);
		LOG.debug("Init board panel: " + boardPanel);

		historyPanel.setSize(boardAndHistory.getWidth() / 2 - 2, boardAndHistory.getHeight());
		boardAndHistory.setRightComponent(historyPanel);
		LOG.debug("Init history panel: " + historyPanel);
	}
}
