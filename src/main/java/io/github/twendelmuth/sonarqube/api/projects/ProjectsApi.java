package io.github.twendelmuth.sonarqube.api.projects;

import java.util.List;

import io.github.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import io.github.twendelmuth.sonarqube.api.NameValuePair;
import io.github.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import io.github.twendelmuth.sonarqube.api.SonarQubeServer;
import io.github.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import io.github.twendelmuth.sonarqube.api.projects.response.ProjectResponse;
import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

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
	public ProjectResponse create(String name, String project) {
		List<NameValuePair> parameters = NameValuePair.listOf("name", name, "project", project);

		return doPostWithErrorHandling(CREATE, parameters, ProjectResponse.class);
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