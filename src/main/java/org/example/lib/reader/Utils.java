package org.example.lib.reader;

import java.util.regex.Pattern;

public class Utils {
	public static  boolean compareDifferenceWithinRange(double a, double b) {
		return Math.abs(a - b) <= 3;
	}

	public static boolean isContainsText(String str) {
		return str != null && str.matches(".*\\p{Print}.*");
	}

	public static final Pattern SPECIAL_CHARACTER_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\s]");

	public static boolean isSpecialCharacter(char ch) {
		return SPECIAL_CHARACTER_PATTERN.matcher(String.valueOf(ch)).find();
	}

}
