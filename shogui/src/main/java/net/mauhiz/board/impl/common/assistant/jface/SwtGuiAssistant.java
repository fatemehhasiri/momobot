package net.mauhiz.board.impl.common.assistant.jface;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import net.mauhiz.board.impl.common.action.ExitAction;
import net.mauhiz.board.impl.common.action.StartAction;
import net.mauhiz.board.impl.common.assistant.AbstractGuiAssistant;
import net.mauhiz.board.impl.common.data.SquareImpl;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;
import net.mauhiz.board.model.gui.BoardGui;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;
import net.mauhiz.util.IAction;
import net.mauhiz.util.ThreadUtils;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.ObjectUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public abstract class SwtGuiAssistant extends AbstractGuiAssistant {
	static class ButtonClearer extends AbstractAction {
		private final Map<Square, Button> lButtons;

		ButtonClearer(final Map<Square, Button> lButtons) {
			this.lButtons = lButtons;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		protected void trun() {
			synchronized (lButtons) {
				for (final Button button : lButtons.values()) {
					button.dispose();
				}

				lButtons.clear();
			}
		}
	}

	class LayoutInitializer extends AbstractAction {
		private final Dimension size;

		LayoutInitializer(final Dimension size) {
			this.size = size;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		public void trun() {
			final GridLayout gridLayout = new GridLayout(size.width, true);
			gridLayout.horizontalSpacing = 0;
			gridLayout.verticalSpacing = 0;
			getShell().setLayout(gridLayout);
			getShell().pack();
		}
	}

	final class ShellRedrawer extends AbstractAction {

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		public void trun() {
			getShell().redraw();
		}
	}

	class SquareAppender extends AbstractAction {
		private final java.awt.Color color;
		private final Map<Square, Button> lButtons;
		private final int x;
		private final int y;

		SquareAppender(final Map<Square, Button> lButtons, final java.awt.Color color, final int x, final int y) {
			this.lButtons = lButtons;
			this.color = color;
			this.x = x;
			this.y = y;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		public void trun() {
			final Button button = new Button(getShell(), SWT.PUSH | SWT.FLAT);
			button.setSize(30, 30);
			button.setBackground(fromAwt(color));
			synchronized (lButtons) {
				lButtons.put(SquareImpl.getInstance(x, y), button);
			}
		}
	}

	static class SquareDisabler extends AbstractAction {
		private final IAction action;
		private final Button button;

		public SquareDisabler(final Button button, final IAction action) {
			this.button = button;
			this.action = action;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		public void trun() {
			button.removeSelectionListener(action);
			button.setEnabled(false);

		}
	}

	static class SquareEnabler extends AbstractAction {
		private final IAction action;
		private final Button button;
		private final IAction old;

		SquareEnabler(final IAction action, final Button button, final IAction old) {
			this.action = action;
			this.button = button;
			this.old = old;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_SYNCHRONOUS;
		}

		@Override
		public void trun() {
			button.addSelectionListener(action);
			if (old == null) {
				button.setEnabled(true);
			} else {
				button.removeSelectionListener(old);
			}
		}
	}

	private final Map<Square, Button> buttons = new HashMap<>();

	private final Shell shell = new Shell(Display.getDefault());

	public SwtGuiAssistant(final BoardGui parent) {
		super(parent);
	}

	@Override
	public void addToHistory(final String value, final IAction action) {
		throw new NotImplementedException();
	}

	@Override
	public void appendSquares(final Iterable<Square> squares, final Dimension size) {
		for (final Square square : squares) {
			final int x = square.getX();
			final int y = size.height - square.getY() - 1;
			new SquareAppender(buttons, getParent().getSquareBgcolor(square), x, y).launch(shell.getDisplay());
		}
	}

	@Override
	public void clear() {
		new ButtonClearer(buttons).launch(shell.getDisplay());

		clearListeners();
	}

	@Override
	public void close() {
		shell.close();
	}

	@Override
	public void decorate(final Square square, final Piece piece) {
		if (piece != null) {
			final Button button = getButton(square);
			decorate(button, piece.getPieceType(), piece.getPlayerType());
		}
	}

	@Override
	public void disableSquare(final Square square) {
		final IAction action = removeListener(square);
		if (action != null) {
			final Button button = getButton(square);
			new SquareDisabler(button, action).launch(button.getDisplay());
		}
	}

	@Override
	public void enableSquare(final Square square, final IAction action) {
		final IAction old = putListener(square, action);

		if (!ObjectUtils.equals(action, old)) {
			final Button button = getButton(square);
			new SquareEnabler(action, button, old).launch(button.getDisplay());
		}
	}

	public Button getButton(final Square at) {
		while (true) {
			Button button;
			synchronized (buttons) {
				button = buttons.get(at);
			}
			if (button == null) {
				ThreadUtils.safeSleep(100);
			} else {
				return button;
			}
		}
	}

	public Shell getShell() {
		return shell;
	}

	@Override
	public void initDisplay() {
		initMenu();
		shell.setText(getParent().getWindowTitle());

		final Dimension defaultSize = getParent().getDefaultSize();
		shell.setSize(defaultSize.width, defaultSize.height);

		final Dimension minSize = getParent().getMinimumSize();
		shell.setMinimumSize(minSize.width, minSize.height);
	}

	@Override
	public void initLayout(final Dimension size) {
		new LayoutInitializer(size).launch(shell.getDisplay());
	}

	@Override
	public void refreshBoard() {
		new ShellRedrawer().launch(shell.getDisplay());
	}

	@Override
	public void start() {
		initDisplay();
		// SWT loop
		shell.open();
		final Display display = shell.getDisplay();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	protected abstract void decorate(Button button, PieceType piece, PlayerType player);

	protected Color fromAwt(final java.awt.Color awtColor) {
		final RGB rgb = new RGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
		return new Color(shell.getDisplay(), rgb);
	}

	protected void initMenu() {
		final Menu menuBar = new Menu(shell, SWT.BAR);
		final MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText("&File");

		final Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);

		final MenuItem fileStartItem = new MenuItem(fileMenu, SWT.PUSH);
		fileStartItem.setText("New Local &Game");
		fileStartItem.addSelectionListener(new StartAction(getParent()));

		final MenuItem fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExitItem.setText("E&xit");
		fileExitItem.addSelectionListener(new ExitAction(getParent()));
		shell.setMenuBar(menuBar);
	}
}
