package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class EveryIthCharacter {

	private static final Path INPUT_PATH = Paths.get("input.txt");

	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

	// This method reads the contents of a file as a string, removes all
	// whitespace and returns the contents as a string.
	private static String readInput(final Path path) throws IOException {
		final String str = new String(Files.readAllBytes(path),
				StandardCharsets.UTF_8);
		return WHITESPACE_PATTERN.matcher(str).replaceAll("");
	}

	private static char unshiftCharacter(final char c, final int unshift) {
		final char d = (char) (c - unshift);
		if (d < 'A') {
			return unshiftCharacter(d, -26);
		} else {
			return d;
		}
	}

	public static void main(final String[] args) throws IOException {
		// The number of entries in the shifts array should match the
		// length of the key. Each entry specifies the amount to shift
		// by.
		final int[] shifts = { 5, 0, 12, 14, 20, 18 }; //FAMOUS
		final String input = readInput(INPUT_PATH);
		final StringBuilder outerStr = new StringBuilder();
		for (int i = 0; i < shifts.length; i++) {
			final StringBuilder innerStr = new StringBuilder();
			for (int j = i; j < input.length(); j += shifts.length) {
				innerStr.append(unshiftCharacter(input.charAt(j), shifts[i]));
			}
			outerStr.append(innerStr);
			System.out.println(innerStr.toString());
		}
	}
}
