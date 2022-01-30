package com.twendelmuth.sonarqube.api;

import com.twendelmuth.sonarqube.api.applications.ApplicationsApi;
import com.twendelmuth.sonarqube.api.ce.ComputeEngineApi;
import com.twendelmuth.sonarqube.api.components.ComponentsApi;
import com.twendelmuth.sonarqube.api.logging.Slf4jSonarQubeLogger;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import com.twendelmuth.sonarqube.api.project.tags.ProjectTagsApi;
import com.twendelmuth.sonarqube.api.projects.ProjectsApi;

public class SonarQubeClient {

	private String serverUrl;

	private String loginToken;

	/**
	 * Creates a new SonarQubeClient instance.
	 * Uses defaults for HTTP implementation, JSONMapper and Logger, see
	 * {@link #getSonarQubeServer()}
	 * {@link #getJsonMapper()}
	 * {@link #getLogger()}
	 * 
	 * 
	 * @param serverUrl Full URL to the SonarQube server including portocol, not ending with '/' - e.g. https://localhost:9000
	 * @param loginToken Login Token for interacting with the SonarQube server, obtainable under Account -> Security
	 */
	public SonarQubeClient(String serverUrl, String loginToken) {
		while (serverUrl.endsWith("/")) {
			serverUrl = serverUrl.substring(0, serverUrl.length() - 1);
		}
		this.serverUrl = serverUrl;
		this.loginToken = loginToken;
	}

	public ComputeEngineApi computeEngine() {
		return new ComputeEngineApi(getSonarQubeServer(), getJsonMapper(), getLogger());
	}

	public ProjectsApi projectsApi() {
		return new ProjectsApi(getSonarQubeServer(), getJsonMapper(), getLogger());
	}

	public ComponentsApi componentsApi() {
		return new ComponentsApi(getSonarQubeServer(), getJsonMapper(), getLogger());
	}

	public ProjectTagsApi projectTagsApi() {
		return new ProjectTagsApi(getSonarQubeServer(), getJsonMapper(), getLogger());
	}

	public ApplicationsApi applicationsApi() {
		return new ApplicationsApi(getSonarQubeServer(), getJsonMapper(), getLogger());
	}

	public SonarQubeServer getSonarQubeServer() {
		return new SonarQubeServerHttpClient(serverUrl, loginToken);
	}

	public SonarQubeLogger getLogger() {
		return new Slf4jSonarQubeLogger();
	}

	public SonarQubeJsonMapper getJsonMapper() {
		return new SonarQubeJacksonMapper();
	}
}
