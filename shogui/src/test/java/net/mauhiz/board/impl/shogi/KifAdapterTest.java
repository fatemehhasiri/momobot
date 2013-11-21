package net.mauhiz.board.impl.shogi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.junit.Test;

public class KifAdapterTest {

	@Test
	public void testImportKif() throws IOException, URISyntaxException {
		final KifAdapter kifa = new KifAdapter();
		final String live = "http://live.shogi.or.jp/asahi/archives/1/kifu/20070707asahi_10.html";
		try (InputStream is = KifImporter.importKif(live)) {
			System.out.println(kifa.readAll(is));
		}
	}

	@Test
	public void testKif() throws IOException {
		final File resFile = new File("src/test/resources/20070707asahi_10.kif");
		final KifAdapter kifa = new KifAdapter();
		try (FileInputStream fis = new FileInputStream(resFile)) {
			System.out.println(kifa.readAll(fis));
		}
	}
}
