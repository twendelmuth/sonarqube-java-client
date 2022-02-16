package io.github.twendelmuth.sonarqube.api.exception;

public class SonarQubeClientJsonException extends Exception {

	private static final long serialVersionUID = 1L;

	public SonarQubeClientJsonException(String message, Throwable cause) {
		super(message, cause);
	}

}
