package net.mauhiz.fbook.challenge;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class Chess2Test {
	@Test
	public void doTest() throws IOException {
		Assert.assertTrue(new Chess2().runTest());
	}
}
