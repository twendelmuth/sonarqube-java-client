package io.github.twendelmuth.sonarqube.api.projects;

import java.util.List;

import io.github.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import io.github.twendelmuth.sonarqube.api.NameValuePair;
import io.github.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import io.github.twendelmuth.sonarqube.api.SonarQubeServer;
import io.github.twendelmuth.sonarqube.api.exception.NotImplementedYetException;
import io.github.twendelmuth.sonarqube.api.exception.SonarQubeValidationException;
import io.github.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import io.github.twendelmuth.sonarqube.api.projects.response.ProjectResponse;
import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ProjectsApi extends AbstractApiEndPoint {
	private static final String CREATE = "/api/projects/create";

	private static final String DELETE = "/api/projects/delete";

	private static final String BULK_DELETE = "/api/projects/bulk_delete";

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

	/**
	 * Delete one or several projects.
	 * Only the 1'000 first items in project filters are taken into account.
	 * Requires 'Administer System' permission.
	 * At least one parameter is required among analyzedBefore, projects and q
	 */
	public SonarApiResponse bulkDelete(ProjectFilterParameter filters) {
		if (filters == null || !filters.hasEnoughParametersForBulkDelete()) {
			throw new SonarQubeValidationException("BulkDelete needs at least one query or analyzedBefore or project parameter");
		}

		return doPostWithErrorHandling(BULK_DELETE, filters.toParameterList(), SonarApiResponse.class);
	}

	/**
	 * Search for projects or views to administrate them.
	 * Requires 'Administer System' permission
	 */
	public void search() {
		throw new NotImplementedYetException();
	}

	/**
	 * Update a project all its sub-components keys.
	 * Requires one of the following permissions:
	 */
	public void updateKey(String from, String to) {
		throw new NotImplementedYetException();
	}

	/**
	 * Updates visibility of a project or view.
	 * Requires 'Project administer' permission on the specified project or view
	 */
	public void updateVisibility(String projectName, ProjectVisibility visibility) {
		throw new NotImplementedYetException();
	}

}
