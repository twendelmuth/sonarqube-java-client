package com.twendelmuth.sonarqube.api;

import com.twendelmuth.sonarqube.api.ce.ComputeEngineApi;
import com.twendelmuth.sonarqube.api.components.ComponentsApi;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import com.twendelmuth.sonarqube.api.project.tags.ProjectTagsApi;
import com.twendelmuth.sonarqube.api.projects.ProjectsApi;

public class SonarQubeClient {

	private String serverUrl;

	private String loginToken;

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

	public final SonarQubeServer getSonarQubeServer() {
		return new SonarQubeServerHttpClient(serverUrl, loginToken);
	}

	public SonarQubeLogger getLogger() {
		return null;
	}

	public SonarQubeJsonMapper getJsonMapper() {
		return new SonarQubeJacksonMapper();
	}
}
