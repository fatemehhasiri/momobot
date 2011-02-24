package net.mauhiz.board.shogi.gui;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Map.Entry;

import net.mauhiz.board.Square;
import net.mauhiz.board.gui.BoardController;
import net.mauhiz.board.shogi.model.ShogiBoard;
import net.mauhiz.board.shogi.model.ShogiOwnedPiece;
import net.mauhiz.board.shogi.model.ShogiPiece;
import net.mauhiz.board.shogi.model.ShogiPlayer;
import net.mauhiz.board.shogi.model.ShogiRule;

public class ShogiBoardController extends BoardController {

    private ShogiPiece selectedPiece;

    public ShogiBoardController(IShogiGui display) {
        super();
        board = new ShogiBoard();
        this.display = display;
    }

    @Override
    protected void clear() {
        getDisplay().clear();
    }

    @Override
    public ShogiBoard getBoard() {
        return (ShogiBoard) board;
    }

    @Override
    protected IShogiGui getDisplay() {
        return (IShogiGui) super.getDisplay();
    }

    @Override
    public void init() {
        clear();

        getBoard().newGame();
        final Dimension size = getBoardSize();

        getDisplay().initLayout(size);

        getDisplay().initPockets();

        for (Square square : getSquares()) {
            getDisplay().appendSquare(square, size);
        }

        refresh();
    }

    @Override
    public void movePiece(final Square to) {
        if (selectedSquare != null) {

            if (board.move(board.getTurn(), selectedSquare, to)) {
                ShogiOwnedPiece currentPiece = getBoard().getOwnedPieceAt(to);

                if (ShogiRule.canPromote(currentPiece, selectedSquare, to)) {
                    getDisplay().showPromotionDialog(this, currentPiece);
                }
                selectedSquare = null;
                refresh();
            }
        } else if (selectedPiece != null) {
            if (getBoard().drop(getBoard().getTurn(), selectedPiece, to)) {
                selectedPiece = null;
                refresh();
            }
        }
    }

    public void promote(ShogiOwnedPiece to) {
        to.setPromoted(true);
        refresh();
    }

    @Override
    public synchronized void refresh() {
        for (Square square : getSquares()) {
            ShogiOwnedPiece op = getBoard().getOwnedPieceAt(square);
            getDisplay().disableSquare(square);

            if (selectedSquare != null) { // from the board
                // available destinations
                ShogiOwnedPiece selected = getBoard().getOwnedPieceAt(selectedSquare);

                if (selected != null && !square.equals(selectedSquare)
                        && ShogiRule.canGo(board, selected, selectedSquare, square)) {
                    getDisplay().addMoveAction(square, this);
                }
            } else if (selectedPiece != null) { // from the pocket
                if (ShogiRule.canDrop(board, square)) {
                    getDisplay().addMoveAction(square, this);
                }
            } else {
                // available pieces
                if (op != null && op.getPlayer() == board.getTurn()) {
                    getDisplay().addSelectAction(square, this);
                }
            }

            getDisplay().decorate(square, op);

        }
        if (selectedSquare != null) {
            getDisplay().addCancelAction(selectedSquare, this);
        }
        refreshPockets();
        getDisplay().refresh();
    }

    private void refreshPockets() {
        for (Entry<ShogiPlayer, Collection<ShogiPiece>> pocket : getBoard().getPockets()) {
            getDisplay().refreshPocket(this, pocket.getKey(), pocket.getValue());
        }
    }

    public void selectPiece(ShogiPiece inPocket) {
        selectedPiece = inPocket;
        refresh();
    }

    @Override
    public void selectPiece(Square at) {
        selectedSquare = at;
        refresh();
    }
}