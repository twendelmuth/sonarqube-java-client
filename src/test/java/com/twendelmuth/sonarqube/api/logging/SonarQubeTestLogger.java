package com.twendelmuth.sonarqube.api.logging;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twendelmuth.sonarqube.api.exception.TestSonarQubeClientJsonException;

public class SonarQubeTestLogger implements SonarQubeLogger {
	private static final Logger LOGGER = LoggerFactory.getLogger(SonarQubeTestLogger.class);

	private List<String> infoMessages = new ArrayList<>();

	private List<String> errorMessages = new ArrayList<>();

	private boolean logUnknownExceptions = true;

	@Override
	public void logInfo(String message) {
		infoMessages.add(message);
	}

	@Override
	public void logError(String error, Throwable exception) {
		if (!(exception instanceof TestSonarQubeClientJsonException) && logUnknownExceptions) {
			LOGGER.error(error, exception);
		}
		errorMessages.add(error);
	}

	public int countLogInfos() {
		return infoMessages.size();
	}

	public int countErrorMessages() {
		return errorMessages.size();
	}

	public void turnOffExceptionLogging() {
		logUnknownExceptions = false;
	}
}
