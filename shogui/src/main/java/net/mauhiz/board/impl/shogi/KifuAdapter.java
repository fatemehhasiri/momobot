package net.mauhiz.board.impl.shogi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.mauhiz.board.model.MoveReader;
import net.mauhiz.board.model.MoveWriter;
import net.mauhiz.board.model.data.Game;

import org.apache.commons.lang.NotImplementedException;

public class KifuAdapter implements MoveReader, MoveWriter {

	@Override
	public Game readAll(final InputStream data) throws IOException {
		throw new NotImplementedException("TODO");
	}

	@Override
	public void readNext(final InputStream data, final Game game) throws IOException {
		throw new NotImplementedException("TODO");
	}

	@Override
	public void writeAll(final OutputStream out, final Game game) throws IOException {
		throw new NotImplementedException("TODO");
	}

	@Override
	public void writeLast(final OutputStream out, final Game game) throws IOException {
		throw new NotImplementedException("TODO");
	}

}
