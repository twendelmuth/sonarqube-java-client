package com.twendelmuth.sonarqube.api.it;

import com.twendelmuth.sonarqube.api.SonarQubeClient;
import com.twendelmuth.sonarqube.api.it.docker.SonarQubeDockerContainer;

public class SonarQubeClientIntegrationHelper {

	private static SonarQubeDockerContainer sonarqubeServer;

	private static SonarQubeClient sonarQubeClient;

	public void start() {
		sonarqubeServer = new SonarQubeDockerContainer();
		sonarqubeServer.startSonarQubeContainer();

		sonarQubeClient = new SonarQubeClient(sonarqubeServer.getServerConnectionString(), sonarqubeServer.getApiToken());
	}

	public SonarQubeClient getSonarQubeClient() {
		if (sonarQubeClient == null) {
			start();
		}

		return sonarQubeClient;
	}

}
