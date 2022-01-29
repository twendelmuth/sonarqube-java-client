package com.twendelmuth.sonarqube.api.exception;

import com.twendelmuth.sonarqube.coverage.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
public class SonarQubeUnexpectedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SonarQubeUnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public SonarQubeUnexpectedException(String message) {
		super(message);
	}

}
