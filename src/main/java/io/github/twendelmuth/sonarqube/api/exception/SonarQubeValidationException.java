package io.github.twendelmuth.sonarqube.api.exception;

/**
 * Exception that indicates that the caller violates some validation rules.
 * This happened before sending any values to the server - anything that can be validated before that.
 *
 */
public class SonarQubeValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SonarQubeValidationException(String message) {
		super(message);
	}

}
