package io.github.twendelmuth.sonarqube.testing.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class UrlTools {
	private UrlTools() {
	}

	public static Map<String, String> extractQueryParameterMap(String url) {
		Map<String, String> parameterMap = new HashMap<>();

		if (StringUtils.isBlank(url) || StringUtils.containsNone(url, "?")) {
			return parameterMap;
		}

		String parameterSide = url.split("\\?")[1];

		Arrays.asList(parameterSide.split("&")).stream()
				.forEach(parameterPair -> putKeyAndValueInMap(parameterMap, parameterPair));

		return parameterMap;
	}

	private static void putKeyAndValueInMap(Map<String, String> parameterMap, String parameterPair) {
		if (parameterPair.contains("=")) {
			parameterMap.put(parameterPair.split("=")[0], parameterPair.split("=")[1]);
		} else {
			parameterMap.put(parameterPair, null);
		}
	}
}
