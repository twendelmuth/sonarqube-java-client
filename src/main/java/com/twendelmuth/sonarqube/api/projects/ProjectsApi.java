package com.twendelmuth.sonarqube.api.projects;

import java.util.HashMap;
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

	/**
	 * Create a project.
	 * Requires 'Create Projects' permission
	 * @param name Name of the project. If name is longer than 500, it is abbreviated.
	 * @param project Key of the project
	 * @return If the operation was successful
	 */
	public boolean create(String name, String project) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("name", name);
		parameters.put("project", project);

		SonarApiResponse response = doPostWithErrorHandling(CREATE, parameters, SonarApiResponse.class);
		return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
	}

	/**
	 * Delete a project.
	 * Requires 'Administer System' permission or 'Administer' permission on the project.
	 * @return If the operation was successful
	 */
	public boolean delete(String projectKey) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("project", projectKey);

		SonarApiResponse response = doPostWithErrorHandling(DELETE, parameters, SonarApiResponse.class);
		return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
	}

}
