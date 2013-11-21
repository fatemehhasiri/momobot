package net.mauhiz.board.impl.common.assistant.jface;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.mauhiz.board.impl.common.action.SelectPocketAction;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.PocketBoardGui;
import net.mauhiz.board.model.gui.PocketGuiAssistant;
import net.mauhiz.util.AbstractAction;
import net.mauhiz.util.ExecutionType;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class PocketSwtGuiAssistant extends SwtGuiAssistant implements PocketGuiAssistant {
	final class PocketClearer extends AbstractAction {
		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		protected void trun() {
			for (final Composite pocket : pockets.values()) {
				for (final Control control : pocket.getChildren()) {
					control.dispose();
				}
			}
		}
	}

	final class PocketRemover extends AbstractAction {
		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		protected void trun() {
			for (final Composite pocket : pockets.values()) {
				pocket.dispose();
			}
		}
	}

	private static final Logger LOG = Logger.getLogger(PocketSwtGuiAssistant.class);
	protected final Map<PlayerType, Composite> pockets = new HashMap<>();

	public PocketSwtGuiAssistant(final PocketBoardGui parent) {
		super(parent);
	}

	@Override
	public void addToPocket(final PieceType pieceType, final PlayerType player) {
		final Button button = new Button(pockets.get(player), SWT.PUSH);
		button.addSelectionListener(new SelectPocketAction(getParent(), pieceType, player));
		decorate(button, pieceType, player);
	}

	@Override
	public void clear() {
		super.clear();
		new PocketRemover().launch(getShell().getDisplay());
	}

	@Override
	public void clearPockets() {
		new PocketClearer().launch(getShell().getDisplay());
	}

	@Override
	public void initLayout(final Dimension size) {
		super.initLayout(size);
		initPockets();
	}

	@Override
	public void refreshPocketActions(final PlayerType player) {
		for (final Entry<PlayerType, Composite> pocket : pockets.entrySet()) {
			for (final Control comp : pocket.getValue().getChildren()) {
				if (comp instanceof Button) {
					LOG.trace("Enabling for player: " + player + " button: " + comp);
					comp.setEnabled(player == pocket.getKey());
				}
			}
		}
	}

	@Override
	public void removeFromPocket(final PieceType piece, final PlayerType player) {
		throw new NotImplementedException();
		//		LOG.debug("Removing from " + player + " pocket: " + piece);
		//		for (Iterator<Entry<Button, Piece>> i = pocketButtons.entrySet().iterator(); i.hasNext();) {
		//			Entry<Button, Piece> candidate = i.next();
		//			Piece inPocket = candidate.getValue();
		//			if (inPocket.getPieceType() == piece && inPocket.getPlayerType() == player) {
		//				Button button = candidate.getKey();
		//				i.remove();
		//				button.dispose();
		//				break;
		//			}
		//		}
	}

	@Override
	protected PocketBoardGui getParent() {
		return (PocketBoardGui) super.getParent();
	}
}
