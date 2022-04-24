package io.github.twendelmuth.sonarqube.api.util.parameter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

public class AbstractParameter {
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssZ");

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");

	/**
	 * Adds parameters to the given StringBuilder.
	 * Takes care of using the correct prefix (`?` or `&`) depending on the size of the given {@link StringBuilder}
	 */
	protected StringBuilder addParameter(StringBuilder builder, String key, String value) {
		if (builder.length() == 0) {
			builder.append("?");
		} else {
			builder.append("&");
		}

		builder.append(key).append("=").append(value);
		return builder;
	}

	protected String formatDateForParameter(ZonedDateTime date) {
		return DATE_TIME_FORMATTER.format(date).replace("+", "%2B");
	}

	protected String formatDateForParameter(LocalDate date) {
		return DATE_FORMATTER.format(date);
	}

	protected String setToString(Set<?> set) {
		return set.stream().map(Object::toString)
				.collect(Collectors.joining(","));

	}

}
