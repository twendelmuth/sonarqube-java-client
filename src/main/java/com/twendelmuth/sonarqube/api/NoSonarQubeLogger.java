package com.twendelmuth.sonarqube.api;

import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;

public class NoSonarQubeLogger implements SonarQubeLogger {

	@Override
	public void logInfo(String message) {
		//no impl
	}

	@Override
	public void logError(String error, Throwable exception) {
		//no impl
	}

}
