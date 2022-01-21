package com.twendelmuth.sonarqube.api.projects;

import java.util.Map;

import com.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ProjectsApi extends AbstractApiEndPoint {
	private static final String CREATE = "/api/projects/create";

	private static final String DELETE = "/api/projects/delete";

	public ProjectsApi(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super(server, jsonMapper, logger);
	}

	public boolean create(String name, String project) {
		SonarApiResponse response = doPostWithErrorHandling(CREATE, Map.of("name", name, "project", project), SonarApiResponse.class);
		return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
	}

	public boolean delete(String projectKey) {
		SonarApiResponse response = doPostWithErrorHandling(DELETE, Map.of("project", projectKey), SonarApiResponse.class);
		return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
	}

}
