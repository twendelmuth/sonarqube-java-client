package com.twendelmuth.sonarqube.api.logging;

public interface SonarQubeLogger {
	void logInfo(String message);

	void logError(String error, Throwable exception);
}
