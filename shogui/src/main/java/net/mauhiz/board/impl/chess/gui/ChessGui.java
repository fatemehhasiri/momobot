package net.mauhiz.board.impl.chess.gui;

import java.awt.Color;

import net.mauhiz.board.impl.chess.ChessGameController;
import net.mauhiz.board.impl.chess.data.ChessBoard;
import net.mauhiz.board.impl.chess.data.ChessPiece;
import net.mauhiz.board.impl.chess.data.ChessPieceType;
import net.mauhiz.board.impl.chess.data.ChessRule;
import net.mauhiz.board.impl.common.controller.AbstractInteractiveBoardGui;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;

/**
 * @author mauhiz
 */
public class ChessGui extends AbstractInteractiveBoardGui {

	public static void main(final String... args) {
		final ChessGui gui = new ChessGui();
		gui.assistant = new SwingChessGuiAssistant(gui);
		gui.getAssistant().start();
	}

	@Override
	public ChessRule getRule() {
		return (ChessRule) super.getRule();
	}

	@Override
	public Color getSquareBgcolor(final Square square) {
		return (square.getX() + square.getY()) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY;
	}

	@Override
	public String getWindowTitle() {
		return "mauhiz' Chess";
	}

	@Override
	public PlayerType sendMove(final Move move) {
		if (move instanceof NormalMove) {
			final NormalMove nmove = (NormalMove) move;

			if (getRule().canPromote(getBoard().getPieceAt(nmove.getFrom()), nmove.getTo())) {
				final ChessPieceType[] promotions = ChessPieceType.getPromotions();
				getAssistant().showPromotionDialog(promotions, nmove);
				return null; // do not send anything yet
			}
		}

		cancelSelection();
		return super.sendMove(move);
	}

	@Override
	protected IChessGuiAssistant getAssistant() {
		return (IChessGuiAssistant) super.getAssistant();
	}

	@Override
	protected ChessBoard getBoard() {
		return (ChessBoard) super.getBoard();
	}

	@Override
	protected ChessGameController newController() {
		return new ChessGameController(this);
	}

	@Override
	protected void refreshSquare(final Square square) {
		final ChessPiece op = getBoard().getPieceAt(square);

		if (isSquareSelected()) { // from the board
			// available destinations
			final Move move = getRule().generateMove(getSelectedSquare(), square, getGame());

			if (move != null) {
				addMoveAction(square, move);
				return;
			}
		} else if (op != null && op.getPlayerType() == getTurn()) { // available pieces
			addSelectAction(square);
			return;
		}

		disableSquare(square);
	}
}
