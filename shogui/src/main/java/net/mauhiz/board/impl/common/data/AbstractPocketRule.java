package net.mauhiz.board.impl.common.data;

import net.mauhiz.board.model.data.Drop;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PocketRule;
import net.mauhiz.board.model.data.Square;

public abstract class AbstractPocketRule extends AbstractRule implements PocketRule {

	public Drop generateMove(PieceType toDrop, Square onTo, Game game) {
		Drop move = new DropImpl(game.getTurn(), toDrop, onTo);
		if (preCheck(move, game.getLastBoard(), game)) {
			return move;
		}
		return null;
	}
}