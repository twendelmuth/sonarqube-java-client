package com.twendelmuth.sonarqube.api.exception;

public class TestSonarQubeClientJsonException extends SonarQubeClientJsonException {
	private static final long serialVersionUID = 1L;

	public TestSonarQubeClientJsonException(String message, Throwable cause) {
		super(message, cause);
	}

	public static TestSonarQubeClientJsonException get() {
		return new TestSonarQubeClientJsonException("Something happened", null);
	}

}
