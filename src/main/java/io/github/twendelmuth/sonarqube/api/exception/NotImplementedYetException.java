package io.github.twendelmuth.sonarqube.api.exception;

import io.github.twendelmuth.sonarqube.coverage.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
public class NotImplementedYetException extends RuntimeException {
	public NotImplementedYetException() {
		super("Not yet implemented");
	}
}
