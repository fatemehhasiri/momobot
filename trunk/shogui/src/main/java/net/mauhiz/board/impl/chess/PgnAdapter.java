package net.mauhiz.board.impl.chess;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mauhiz.board.impl.chess.data.ChessBoard.Status;
import net.mauhiz.board.impl.chess.data.ChessGame;
import net.mauhiz.board.impl.chess.data.ChessPiece;
import net.mauhiz.board.impl.chess.data.ChessPieceType;
import net.mauhiz.board.impl.chess.data.ChessPlayerType;
import net.mauhiz.board.impl.chess.data.ChessRule;
import net.mauhiz.board.impl.common.data.NormalMoveImpl;
import net.mauhiz.board.impl.common.data.SquareImpl;
import net.mauhiz.board.model.MoveReader;
import net.mauhiz.board.model.MoveWriter;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.Square;

import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

public class PgnAdapter implements MoveReader, MoveWriter {
	private static final Pattern CASTLE = Pattern.compile("O-O(-O)?([+#=])?");
	private static final Pattern SAN_MOVE = Pattern
			.compile("([a-hRNBQKP])?([a-h]?[1-8]?)?(x)?([a-h])([1-8])(=[RNBQ])?([+#=])?");

	private static Square findFrom(ChessGame game, String fromPosition, Square to, ChessPieceType moved,
			ChessPlayerType player) {
		if (fromPosition != null && fromPosition.length() == 2) {
			return positionToSquare(fromPosition.charAt(0), fromPosition.charAt(2));
		}
		char position = fromPosition == null ? 0 : fromPosition.charAt(0);
		int col = 0;
		int row = 0;
		if (Character.isDigit(position)) {
			row = position - '1';
		} else {
			col = position - 'a';
		}
		for (Square square : game.getBoard().getSquares()) {
			if (col > 0 && square.getX() != col || row != 0 && square.getY() != row) {
				continue;
			}
			ChessPiece cop = game.getBoard().getPieceAt(square);
			if (cop == null || cop.getPlayerType() != player || cop.getPieceType() != moved) {
				continue;
			}
			if (game.getRule().canGo(game.getBoard(), square, to)) {
				return square;
			}
		}
		return null;
	}

	private static Square positionToSquare(char col, char row) {
		return SquareImpl.getInstance(col - 'a', row - '1');
	}

	static Status readStatus(String statusLine) {
		if (statusLine != null) {
			if (StringUtils.contains(statusLine, '#')) {
				return Status.MATE;
			} else if (StringUtils.contains(statusLine, '+')) {
				return Status.CHECK;
			} else if (StringUtils.contains(statusLine, '=')) {
				return Status.DRAW;
			}
		}

		return null;
	}

	public static char[] squareToPosition(Square square) {
		return new char[] { (char) (square.getX() + 'a'), (char) (square.getY() + '1') };
	}

	@Override
	public Move read(Game game, String pgnMove) {
		if (!(game instanceof ChessGame)) {
			throw new IllegalClassException(ChessGame.class, game.getClass());
		}
		ChessPlayerType player = (ChessPlayerType) game.getTurn();

		Move cm;
		Matcher castle = CASTLE.matcher(pgnMove);
		if (castle.matches()) {
			boolean great = castle.group(1) != null;
			cm = new Castle(player, great);
			//            String statusLine = castle.group(2);
			// status = readStatus(statusLine);
		} else {
			Matcher m = SAN_MOVE.matcher(pgnMove);
			if (m.matches()) {

				int i = 0;
				String movedPiece = m.group(++i);
				ChessPieceType moved = ChessPieceType.fromName(movedPiece == null ? "P" : movedPiece);
				String fromPosition = m.group(++i);
				++i; // capture
				char toX = m.group(++i).charAt(0);
				char toY = m.group(++i).charAt(0);
				Square to = positionToSquare(toX, toY);
				Square from = findFrom((ChessGame) game, fromPosition, to, moved, player);

				cm = new NormalMoveImpl(player, from, to);
				String promotionType = m.group(++i);
				if (promotionType != null) {
					ChessPieceType promotion = ChessPieceType.fromName(promotionType.substring(1));
					cm = new PromoteMove((NormalMove) cm, promotion);
				}
				//                String statusLine = m.group(++i);
				// status = readStatus(statusLine);
			} else {
				return null;
			}
		}

		return cm;
	}

	/**
	 * @param pgnData no pairs, no comments
	 */
	public List<Move> read(String pgnData) {
		String[] pgnMoves = pgnData.split(" ");
		ChessGame game = new ChessGame(new ChessRule());

		for (int i = 0; i < pgnMoves.length; i++) {
			if (i % 3 == 0) {
				// move #
				continue;
			}

			Move move = read(game, pgnMoves[i]);
			if (game.applyMove(move) == null) {
				throw new IllegalArgumentException("Invalid move at " + i / 3);
			}
		}

		return game.getMoves();
	}

	@Override
	public String write(Game game, Move move) {
		throw new NotImplementedException();
	}
}
