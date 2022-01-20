package com.twendelmuth.sonarqube.api.exception;

public class SonarQubeClientException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SonarQubeClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
