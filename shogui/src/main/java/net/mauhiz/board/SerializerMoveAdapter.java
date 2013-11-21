package net.mauhiz.board;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;

import net.mauhiz.board.model.MoveReader;
import net.mauhiz.board.model.MoveWriter;
import net.mauhiz.board.model.data.Game;
import net.mauhiz.board.model.data.Move;

import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;

/**
 * Default serialization for moves
 * @author mauhiz
 */
public class SerializerMoveAdapter implements MoveReader, MoveWriter {

	@Override
	public Game readAll(final InputStream data) throws IOException {
		final ObjectInputStream in = new ObjectInputStream(data);
		try {
			return (Game) in.readObject();
		} catch (final ClassNotFoundException ex) {
			throw new SerializationException(ex);
		} catch (final ObjectStreamException e) {
			return null; // EOF
		}
	}

	@Override
	public void readNext(final InputStream data, final Game game) throws IOException {
		game.applyMove(readMove(data));
	}

	@Override
	public void writeAll(final OutputStream out, final Game game) throws IOException {
		for (final Move move : game.getMoves()) {
			writeMove(out, move);
		}
	}

	@Override
	public void writeLast(final OutputStream out, final Game game) throws IOException {
		writeMove(out, game.getLastMove());
	}

	protected Move readMove(final InputStream data) throws IOException {
		final ObjectInputStream in = new ObjectInputStream(data);
		try {
			return (Move) in.readObject();
		} catch (final ClassNotFoundException ex) {
			throw new SerializationException(ex);
		} catch (final ObjectStreamException e) {
			return null; // EOF
		}
	}

	protected void writeMove(final OutputStream out, final Move move) throws IOException {
		if (move instanceof Serializable) {
			out.write(SerializationUtils.serialize((Serializable) move));
		} else {
			throw new IllegalClassException(Serializable.class, move);
		}
	}
}
