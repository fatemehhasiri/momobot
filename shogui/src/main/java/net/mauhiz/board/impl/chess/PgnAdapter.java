package net.mauhiz.board.impl.chess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mauhiz.board.impl.chess.data.ChessBoard.Status;
import net.mauhiz.board.impl.chess.data.ChessGame;
import net.mauhiz.board.impl.chess.data.ChessPieceType;
import net.mauhiz.board.impl.chess.data.ChessRule;
import net.mauhiz.board.impl.common.data.NormalMoveImpl;
import net.mauhiz.board.impl.common.data.SquareImpl;
import net.mauhiz.board.model.MoveReader;
import net.mauhiz.board.model.MoveWriter;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.Move;
import net.mauhiz.board.model.data.NormalMove;
import net.mauhiz.board.model.data.Piece;
import net.mauhiz.board.model.data.PieceType;
import net.mauhiz.board.model.data.PlayerType;
import net.mauhiz.board.model.data.Square;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

public enum PgnAdapter implements MoveReader, MoveWriter {
	INSTANCE;
	private static final Pattern CASTLE = Pattern.compile("O-O(-O)?([+#=])?");
	private static final Pattern SAN_MOVE = Pattern
			.compile("([a-hRNBQKP])?([a-h]?[1-8]?)?(x)?([a-h])([1-8])(=[RNBQ])?([+#=])?");

	/**
	 * @param square
	 * @return a pair of characters
	 */
	public static int[] squareToPosition(final Square square) {
		return new int[] { square.getX() + 'a', square.getY() + '1' };
	}

	static Status readStatus(final String statusLine) {
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

	private static Square findFrom(final Game game, final String fromPosition, final Square to, final PieceType moved,
			final PlayerType player) {
		if (fromPosition != null && fromPosition.length() == 2) {
			return positionToSquare(fromPosition.codePointAt(0), fromPosition.codePointAt(2));
		}
		final int position = fromPosition == null ? 0 : fromPosition.codePointAt(0);
		int col = 0;
		int row = 0;
		if (Character.isDigit(position)) {
			row = position - '1';
		} else {
			col = position - 'a';
		}
		for (final Square square : game.getLastBoard().getSquares()) {
			if (col > 0 && square.getX() != col || row != 0 && square.getY() != row) {
				continue;
			}
			final Piece cop = game.getLastBoard().getPieceAt(square);
			if (cop == null || cop.getPlayerType() != player || cop.getPieceType() != moved) {
				continue;
			}
			if (game.getRule().generateMove(square, to, game) != null) {
				return square;
			}
		}
		return null;
	}

	private static Square positionToSquare(final int col, final int row) {
		return SquareImpl.getInstance(col - 'a', row - '1');
	}

	private static void read(final Game game, final String pgnMove) {
		final PlayerType player = game.getTurn();
		Move cm;
		final Matcher castle = CASTLE.matcher(pgnMove);
		if (castle.matches()) {
			final boolean great = castle.group(1) != null;
			cm = new Castle(player, great);
			//            String statusLine = castle.group(2);
			// status = readStatus(statusLine);
		} else {
			final Matcher m = SAN_MOVE.matcher(pgnMove);
			if (m.matches()) {

				int i = 0;
				final String movedPiece = m.group(++i);
				final ChessPieceType moved = ChessPieceType.fromName(movedPiece == null ? "P" : movedPiece);
				final String fromPosition = m.group(++i);
				++i; // capture
				final int toX = m.group(++i).codePointAt(0);
				final int toY = m.group(++i).codePointAt(0);
				final Square to = positionToSquare(toX, toY);
				final Square from = findFrom(game, fromPosition, to, moved, player);

				cm = new NormalMoveImpl(player, from, to);
				final String promotionType = m.group(++i);
				if (promotionType != null) {
					final ChessPieceType promotion = ChessPieceType.fromName(promotionType.substring(1));
					cm = new PromoteMove((NormalMove) cm, promotion);
				}
				//                String statusLine = m.group(++i);
				// status = readStatus(statusLine);
			} else {
				return;
			}
		}
		game.applyMove(cm);
	}

	@Override
	public Game readAll(final InputStream data) throws IOException {
		final Game game = new ChessGame(new ChessRule());
		final String pgnData = IOUtils.toString(data);
		final String[] pgnMoves = pgnData.split(" ");

		for (int i = 0; i < pgnMoves.length; i++) {
			if (i % 3 == 0) {
				// move #
				continue;
			}

			read(game, pgnMoves[i]);
		}

		return game;
	}

	@Override
	public void readNext(final InputStream data, final Game game) throws IOException {
		final String pgnMove = IOUtils.toString(data);
		read(game, pgnMove);
	}

	@Override
	public void writeAll(final OutputStream out, final Game game) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public void writeLast(final OutputStream out, final Game game) throws IOException {
		throw new NotImplementedException();
	}
}
