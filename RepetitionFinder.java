package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class RepetitionFinder {

	private static final Path INPUT_PATH = Paths.get("input.txt");

	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

	// This method reads the contents of a file as a string, removes all
	// whitespace and returns the contents as a string.
	private static String readInput(final Path path) throws IOException {
		final String str = new String(Files.readAllBytes(path),
				StandardCharsets.UTF_8);
		return WHITESPACE_PATTERN.matcher(str).replaceAll("");
	}

	// This method finds all repeated substrings of a given length in a string
	// and returns a map from substrings to counts.
	private static Map<String, Integer> findRepetitions(final String str,
			final int length) {
		final Map<String, Integer> map = new HashMap<>();
		final int limit = str.length() - length + 1;
		for (int i = 0; i < limit; i++) {
			final String sub = str.substring(i, i + length);
			Integer counter = map.get(sub);
			if (counter == null) {
				counter = 0;
			}
			map.put(sub, ++counter);
		}
		final Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			final String k = it.next();
			if (map.get(k) == 1) {
				it.remove();
			}
		}
		return map;
	}

	public static void main(final String[] args) throws IOException {
		final String input = readInput(INPUT_PATH);
		for (int i = input.length() / 2; i > 1; i--) {
			final Map<String, Integer> repetitions = findRepetitions(input, i);
			if (repetitions.size() >= 1) {
				System.out.println(i + ": " + repetitions);
			}
		}
	}
}
