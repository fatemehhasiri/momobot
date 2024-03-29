package net.mauhiz.contest.facebook.puzzle;

import java.io.IOException;
import java.io.PrintWriter;

import net.mauhiz.contest.facebook.SingleLinePuzzle;

public class Hoppity extends SingleLinePuzzle {

	public static void main(String... args) throws IOException {
		new Hoppity().run(args);
	}

	@Override
	public String getName() {
		return "hoppity";
	}
	
	@Override
	protected void doProblem(String problemLine, PrintWriter output) {
		long number = Long.parseLong(problemLine);
		for (int i = 1; i <= number; i++) {
			if (i % 3 == 0) {
				if (i % 5 == 0) {
					output.println("Hop");
				} else {
					output.println("Hoppity");
				}
			} else if (i % 5 == 0) {
				output.println("Hophop");
			}
		}
	}
}
