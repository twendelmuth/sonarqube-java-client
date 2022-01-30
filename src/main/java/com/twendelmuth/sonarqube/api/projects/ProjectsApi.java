package com.twendelmuth.sonarqube.api.projects;

import java.util.List;

import com.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import com.twendelmuth.sonarqube.api.NameValuePair;
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
	public SonarApiResponse create(String name, String project) {
		List<NameValuePair> parameters = NameValuePair.listOf("name", name, "project", project);

		return doPostWithErrorHandling(CREATE, parameters, SonarApiResponse.class);
	}

	/**
	 * Delete a project.
	 * Requires 'Administer System' permission or 'Administer' permission on the project.
	 * @return If the operation was successful
	 */
	public SonarApiResponse delete(String projectKey) {
		List<NameValuePair> parameters = NameValuePair.listOf("project", projectKey);
		return doPostWithErrorHandling(DELETE, parameters, SonarApiResponse.class);
	}

}
