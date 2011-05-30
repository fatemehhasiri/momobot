package net.mauhiz.board.impl.go;

import net.mauhiz.board.impl.common.AbstractPocketGameController;
import net.mauhiz.board.impl.common.gui.AbstractInteractiveBoardGui;
import net.mauhiz.board.impl.go.data.GoGame;
import net.mauhiz.board.impl.go.data.GoRule;
import net.mauhiz.board.model.BoardIO;

public class GoGameController extends AbstractPocketGameController {

    public GoGameController(BoardIO display) {
        super(display);
    }

    @Override
    public AbstractInteractiveBoardGui getBoardIO() {
        return (AbstractInteractiveBoardGui) super.getBoardIO();
    }

    @Override
    public GoGame getGame() {
        return (GoGame) game;
    }

    @Override
    protected GoGame newGame() {
        return new GoGame(new GoRule());
    }
}