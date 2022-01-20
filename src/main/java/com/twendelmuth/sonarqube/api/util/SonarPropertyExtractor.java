package com.twendelmuth.sonarqube.api.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SonarPropertyExtractor {
	private SonarPropertyExtractor() {
	}

	private static Pattern getPatternForProperty(String property) {
		return Pattern.compile("^(.*)(" + property + "=)(.*)$");
	}

	private static String extractPropertyFromString(String property, String string) {
		if (string != null) {
			Pattern pattern = getPatternForProperty(property);
			Optional<String> matchedLine = Arrays.stream(string.split("\n")).filter(singleLine -> pattern.matcher(singleLine).find()).findFirst();
			if (matchedLine.isPresent()) {
				Matcher matcher = pattern.matcher(matchedLine.get());
				matcher.find();
				return matcher.group(3);
			}
		}

		return "";
	}

	public static String getSonarScannerApp(String string) {
		return extractPropertyFromString("sonar.scanner.app", string);
	}

	public static String getSonarScannerVersion(String string) {
		return extractPropertyFromString("sonar.scanner.appVersion", string);
	}
}
