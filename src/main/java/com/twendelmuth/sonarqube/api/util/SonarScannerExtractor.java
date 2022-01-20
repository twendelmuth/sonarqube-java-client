package com.twendelmuth.sonarqube.api.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SonarScannerExtractor {

	private static Pattern APP_REGEX = Pattern.compile("^(.*)(sonar.scanner.app=)(.*)$");

	private static Pattern VERSION_REGEX = Pattern.compile("^(.*)(sonar.scanner.appVersion=)(.*)$");

	public static String getSonarScannerApp(String string) {
		if (string != null) {
			Optional<String> matchedLine = Arrays.stream(string.split("\n")).filter(singleLine -> APP_REGEX.matcher(singleLine).find()).findFirst();
			if (matchedLine.isPresent()) {
				Matcher matcher = APP_REGEX.matcher(matchedLine.get());
				matcher.find();
				return matcher.group(3);
			}
		}

		return "";
	}

	public static String getSonarScannerVersion(String string) {
		if (string != null) {
			Optional<String> matchedLine = Arrays.stream(string.split("\n")).filter(singleLine -> VERSION_REGEX.matcher(singleLine).find())
					.findFirst();
			if (matchedLine.isPresent()) {
				Matcher matcher = VERSION_REGEX.matcher(matchedLine.get());
				matcher.find();
				return matcher.group(3);
			}
		}

		return "";
	}
}
