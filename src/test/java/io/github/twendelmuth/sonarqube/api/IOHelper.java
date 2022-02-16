package io.github.twendelmuth.sonarqube.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class IOHelper {
	public static String getStringFromResource(Class<?> clazz, String resource) {

		try {
			return IOUtils.toString(clazz.getResourceAsStream(resource), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read resource during test", e);
		}
	}
}
