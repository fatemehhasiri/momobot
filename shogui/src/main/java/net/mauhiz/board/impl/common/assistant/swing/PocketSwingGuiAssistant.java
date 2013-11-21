package net.mauhiz.board.impl.common.assistant.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.mauhiz.board.impl.common.action.SelectPocketAction;
import net.mauhiz.board.impl.common.assistant.swing.button.RotatingJButton;
import net.mauhiz.board.impl.common.data.DefaultPiece;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.gui.PocketBoardGui;
import net.mauhiz.board.model.gui.PocketGuiAssistant;

import org.apache.log4j.Logger;

public abstract class PocketSwingGuiAssistant extends SwingGuiAssistant implements PocketGuiAssistant {

	private static final Logger LOG = Logger.getLogger(PocketSwingGuiAssistant.class);
	protected final JPanel boardAndPocketsPanel = new JPanel();
	private final Map<RotatingJButton, Piece> pocketButtons = new HashMap<>();
	private final SortedMap<PlayerType, JPanel> pockets = new TreeMap<>();

	public PocketSwingGuiAssistant(final PocketBoardGui parent) {
		super(parent);
	}

	@Override
	public void addToPocket(final PieceType pieceType, final PlayerType player) {
		final RotatingJButton button = new RotatingJButton();
		button.setBackground(getParent().getSquareBgcolor(null));
		button.setSize(30, 30);
		decorate(button, pieceType, player);
		button.addActionListener(new SelectPocketAction(getParent(), pieceType, player));
		getPocket(player).add(button);
		pocketButtons.put(button, new DefaultPiece(player, pieceType));
		LOG.debug("Adding to pocket: " + pieceType);
		refreshPocketActions(player);
	}

	@Override
	public void clear() {
		super.clear();
		clearPockets();
	}

	@Override
	public void clearPockets() {
		LOG.trace("Emptying pockets");
		synchronized (pockets) {
			for (final JPanel pocket : pockets.values()) {
				pocket.removeAll();
			}
		}
		synchronized (pocketButtons) {
			pocketButtons.clear();
		}
	}

	@Override
	public void initLayout(final Dimension size) {
		boardAndPocketsPanel.setLayout(new BoxLayout(boardAndPocketsPanel, BoxLayout.Y_AXIS));
		LOG.debug("Board and pockets layout: " + boardAndPocketsPanel.getLayout());
		super.initLayout(size);
		initPockets();
	}

	@Override
	public void refreshPocketActions(final PlayerType player) {
		synchronized (pockets) {
			for (final Entry<PlayerType, JPanel> pocket : pockets.entrySet()) {
				for (final Component comp : pocket.getValue().getComponents()) {
					if (comp instanceof RotatingJButton) {
						comp.setEnabled(player == pocket.getKey());
					}
				}
			}
		}
	}

	@Override
	public void removeFromPocket(final PieceType piece, final PlayerType player) {
		LOG.debug("Removing from " + player + " pocket: " + piece);
		synchronized (pocketButtons) {
			for (final Iterator<Entry<RotatingJButton, Piece>> i = pocketButtons.entrySet().iterator(); i.hasNext();) {
				final Entry<RotatingJButton, Piece> candidate = i.next();
				final Piece inPocket = candidate.getValue();
				if (inPocket.getPieceType() == piece && inPocket.getPlayerType() == player) {
					final RotatingJButton button = candidate.getKey();
					i.remove();
					getPocket(player).remove(button);
					button.removeActionListener(null); // clear listeners
					break;
				}
			}
		}
	}

	protected JPanel addPocket(final PlayerType playerType, final JPanel pocket) {
		synchronized (pockets) {
			return pockets.put(playerType, pocket);
		}
	}

	@Override
	protected PocketBoardGui getParent() {
		return (PocketBoardGui) super.getParent();
	}

	protected JPanel getPocket(final PlayerType playerType) {
		synchronized (pockets) {
			return pockets.get(playerType);
		}
	}

	@Override
	protected void initPanels() {
		boardAndHistory.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		boardAndHistory.setSize(frame.getWidth() - 2, frame.getHeight() - 2);
		boardAndHistory.setDividerLocation(0.8);
		frame.add(boardAndHistory);
		LOG.debug("Init board and history split panel: " + boardAndHistory);

		boardAndHistory.setLeftComponent(boardAndPocketsPanel);
		LOG.debug("Init Board and pockets: " + boardAndPocketsPanel);

		boardAndHistory.setRightComponent(historyPanel);
		LOG.debug("Init History panel: " + historyPanel);

		boardAndPocketsPanel.add(boardPanel);
		LOG.debug("Init Board panel: " + boardPanel);
	}
}
