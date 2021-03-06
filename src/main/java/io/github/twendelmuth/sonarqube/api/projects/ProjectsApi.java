package io.github.twendelmuth.sonarqube.api.projects;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import io.github.twendelmuth.sonarqube.api.NameValuePair;
import io.github.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import io.github.twendelmuth.sonarqube.api.SonarQubeServer;
import io.github.twendelmuth.sonarqube.api.components.response.SearchProjectResponse;
import io.github.twendelmuth.sonarqube.api.exception.SonarQubeValidationException;
import io.github.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import io.github.twendelmuth.sonarqube.api.projects.ProjectFilterParameter.ProjectFilterBuilder;
import io.github.twendelmuth.sonarqube.api.projects.ProjectFilterParameter.ProjectSearchFilterBuilder;
import io.github.twendelmuth.sonarqube.api.projects.response.ProjectResponse;
import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ProjectsApi extends AbstractApiEndPoint {
	private static final String PROJECT_PARAMETER = "project";

	private static final String NAME_PARAMETER = "name";

	private static final String CREATE = "/api/projects/create";

	private static final String DELETE = "/api/projects/delete";

	private static final String BULK_DELETE = "/api/projects/bulk_delete";

	private static final String UPDATE_KEY = "/api/projects/update_key";

	private static final String UPDATE_VISIBILITY = "/api/projects/update_visibility";

	private static final String SEARCH = "/api/projects/search";

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
		List<NameValuePair> parameters = NameValuePair.listOf(NAME_PARAMETER, name, PROJECT_PARAMETER, project);

		return doPostWithErrorHandling(CREATE, parameters, ProjectResponse.class);
	}

	/**
	 * Delete a project.
	 * Requires 'Administer System' permission or 'Administer' permission on the project.
	 * @return If the operation was successful
	 */
	public SonarApiResponse delete(String projectKey) {
		List<NameValuePair> parameters = NameValuePair.listOf(PROJECT_PARAMETER, projectKey);
		return doPostWithErrorHandling(DELETE, parameters, SonarApiResponse.class);
	}

	/**
	 * Delete one or several projects.
	 * Only the 1'000 first items in project filters are taken into account.
	 * Requires 'Administer System' permission.
	 * At least one parameter is required among analyzedBefore, projects and q
	 */
	public SonarApiResponse bulkDelete(ProjectFilterBuilder filters) {
		if (filters == null || !filters.build().hasEnoughParametersForBulkDelete()) {
			throw new SonarQubeValidationException("BulkDelete needs at least one query or analyzedBefore or project parameter");
		}

		return doPostWithErrorHandling(BULK_DELETE, filters.build().toParameterList(), SonarApiResponse.class);
	}

	/**
	 * Search for projects or views to administrate them.
	 * Requires 'Administer System' permission
	 */
	public SearchProjectResponse search(ProjectSearchFilterBuilder filters) {
		String parameters = "";
		if (filters != null) {
			parameters = filters.build().toParameterString();
		}

		return doGetWithErrorHandling(SEARCH + parameters, SearchProjectResponse.class);
	}

	/**
	 * Update a project all its sub-components keys.
	 * Requires one of the following permissions:
	 */
	public SonarApiResponse updateKey(String from, String to) {
		if (StringUtils.isBlank(from)) {
			throw new SonarQubeValidationException("From can't be empty");
		}
		if (StringUtils.isBlank(to)) {
			throw new SonarQubeValidationException("To can't be empty");
		}

		return doPostWithErrorHandling(UPDATE_KEY, NameValuePair.listOf("from", from, "to", to), SonarApiResponse.class);
	}

	/**
	 * Updates visibility of a project or view.
	 * Requires 'Project administer' permission on the specified project or view
	 */
	public SonarApiResponse updateVisibility(String projectName, ProjectVisibility visibility) {
		if (StringUtils.isBlank(projectName)) {
			throw new SonarQubeValidationException("ProjectName can't be empty");
		}
		if (visibility == null) {
			throw new SonarQubeValidationException("ProjectVisibility can't be null");
		}

		return doPostWithErrorHandling(UPDATE_VISIBILITY, NameValuePair.listOf(PROJECT_PARAMETER, projectName, "visibility", visibility.getApiName()),
				SonarApiResponse.class);
	}

}
