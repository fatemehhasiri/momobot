package net.mauhiz.board.impl.chess.data;

import net.mauhiz.board.impl.chess.Castle;
import net.mauhiz.board.impl.common.data.AbstractGame;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;

public class ChessGame extends AbstractGame {

	public ChessGame(final ChessRule rule) {
		super(rule);
	}

	public boolean kingHasMoved(final PlayerType kingOwner) {
		for (final Move move : moves) {
			if (move.getPlayerType() == kingOwner) {
				if (move instanceof Castle) {
					return true;
				} else if (move instanceof NormalMove) {
					final NormalMove nmove = (NormalMove) move;
					if (nmove.getFrom().getX() == 4
							&& nmove.getFrom().getY() == (kingOwner == ChessPlayerType.WHITE ? 0 : 7)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean rookHasMoved(final PlayerType rookOwner, final Square rookStart) {
		for (final Move move : moves) {
			if (move.getPlayerType() == rookOwner && move instanceof NormalMove) {
				final NormalMove nmove = (NormalMove) move;
				if (nmove.getFrom() == rookStart) {
					return true;
				}
			}
		}
		return false;
	}

}
