package net.mauhiz.board.impl.common.assistant.applet;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import net.mauhiz.board.impl.common.action.SelectPocketAction;
import net.mauhiz.board.impl.common.assistant.swing.button.RotatingJButton;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.PocketBoardGui;
import net.mauhiz.board.model.gui.PocketGuiAssistant;

public abstract class PocketAwtGuiAssistant extends AwtGuiAssistant implements PocketGuiAssistant {

	protected Map<PlayerType, JPanel> pockets = new HashMap<>();

	public PocketAwtGuiAssistant(final PocketBoardGui parent) {
		super(parent);
	}

	@Override
	public void addToPocket(final PieceType pieceType, final PlayerType player) {
		final RotatingJButton button = new RotatingJButton();
		decorate(button, pieceType, player);
		pockets.get(player).add(button);
		button.addActionListener(new SelectPocketAction(getParent(), pieceType, player));
	}

	@Override
	public void clearPockets() {
		for (final JPanel pocket : pockets.values()) {
			pocket.removeAll();
		}
	}

	@Override
	public void initLayout(final Dimension size) {
		super.initLayout(size);
		initPockets();
	}

	@Override
	public void refreshPocketActions(final PlayerType player) {
	}

	@Override
	public void removeFromPocket(final PieceType piece, final PlayerType player) {
	}

	@Override
	protected PocketBoardGui getParent() {
		return (PocketBoardGui) super.getParent();
	}
}
