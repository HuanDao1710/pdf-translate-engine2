package org.example.lib.reader;

public class Utils {
	public static  boolean compareDifferenceWithinRange(double a, double b) {
		return Math.abs(a - b) <= 3;
	}

	public static boolean isContainsText(String str) {
		return str != null && str.matches(".*\\p{Print}.*");
	}

}
