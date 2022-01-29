package com.twendelmuth.sonarqube.api.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jSonarQubeLogger implements SonarQubeLogger {
	private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jSonarQubeLogger.class);

	@Override
	public void logInfo(String message) {
		LOGGER.info(message);
	}

	@Override
	public void logError(String error, Throwable exception) {
		LOGGER.error(error, exception);
	}

}
