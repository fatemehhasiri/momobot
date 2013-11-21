package net.mauhiz.board.impl.shogi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.mauhiz.util.NetUtils;

import org.apache.commons.lang.StringUtils;

public class KifImporter {
	/**
	 * so.addVariable("kifu", "data/jo_oui20110518.kif.Z");
	 * @param flashUrl
	 * @throws IOException 
	 */
	public static InputStream importKif(final String flashUrl) throws URISyntaxException, IOException {
		final URI flashUri = new URI(flashUrl);

		final String page = NetUtils.doHttpGet(flashUri);
		final String dataPath = StringUtils.substringBetween(page, "addVariable(\"kifu\", \"", ".Z\"");
		final String dataRoot = StringUtils.substringBeforeLast(flashUrl, "/");
		final String dataUrl = dataRoot + "/" + dataPath;
		return new URL(dataUrl).openStream();
	}
}
