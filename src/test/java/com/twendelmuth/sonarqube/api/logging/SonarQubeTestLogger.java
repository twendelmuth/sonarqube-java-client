package com.twendelmuth.sonarqube.api.logging;

import java.util.ArrayList;
import java.util.List;

public class SonarQubeTestLogger implements SonarQubeLogger {
	private List<String> infoMessages = new ArrayList<>();

	private List<String> errorMessages = new ArrayList<>();

	@Override
	public void logInfo(String message) {
		infoMessages.add(message);
	}

	@Override
	public void logError(String error, Throwable exception) {
		errorMessages.add(error);
	}

	public int countLogInfos() {
		return infoMessages.size();
	}

	public int countErrorMessages() {
		return errorMessages.size();
	}
}
