package com.twendelmuth.sonarqube.api.applications;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import com.twendelmuth.sonarqube.api.NameValuePair;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.applications.response.ApplicationResponse;
import com.twendelmuth.sonarqube.api.exception.SonarQubeUnexpectedException;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

/**
 * Requires at least SonarQube Developer Edition.
 *
 */
public class ApplicationsApi extends AbstractApiEndPoint {
	private static final String ADD_APPLICATION = "/api/applications/create";

	private static final String ADD_PROJECT = "/api/applications/add_project";

	private static final String DELETE_APPLICATION = "/api/applications/delete";

	public ApplicationsApi(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super(server, jsonMapper, logger);
	}

	/**
	 * Add a project to an application.
	 * Requires 'Administrator' permission on the application
	 * @param application Key of the application
	 * @param project Key of the project
	 * @return If call was successful
	 */
	public SonarApiResponse addProject(String application, String project) {
		if (StringUtils.isEmpty(application)) {
			throw new SonarQubeUnexpectedException("Application cannot be empty");
		}

		if (StringUtils.isEmpty(project)) {
			throw new SonarQubeUnexpectedException("Project cannot be empty");
		}

		List<NameValuePair> parameters = NameValuePair.listOf("application", application, "project", project);

		return doPostWithErrorHandling(ADD_PROJECT, parameters, SonarApiResponse.class);
	}

	/**
	 * Create a new application.
	 * Requires 'Administer System' permission or 'Create Applications' permission
	 * @param name Application name
	 * @param description Application description <strong>(optional)</strong>
	 * @param key Application key. A suitable key will be generated if not provided <strong>(optional)</strong>
	 * @param visibility Whether the created application should be visible to everyone, or only specific user/groups. If no visibility is specified, the default visibility will be used. <strong>(optional)</strong>
	 */
	public ApplicationResponse createApplication(String name, String description, String key, ApplicationVisibility visibility) {
		if (StringUtils.isBlank(name)) {
			throw new SonarQubeUnexpectedException("Name cannot be empty");
		}

		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new NameValuePair("name", name));
		if (StringUtils.isNotBlank(description)) {
			parameters.add(new NameValuePair("description", description));
		}
		if (StringUtils.isNotBlank(key)) {
			parameters.add(new NameValuePair("key", key));
		}
		if (visibility != null) {
			parameters.add(new NameValuePair("visibility", visibility.getApiName()));
		}

		return doPostWithErrorHandling(ADD_APPLICATION, parameters, ApplicationResponse.class);
	}

	/**
	 * Create a new branch on a given application.
	 * Requires 'Administrator' permission on the application and 'Browse' permission on its child projects
	 * @param application Application key
	 * @param branch Branch name <strong>(max-length: 255)</strong>
	 * @param applicationProjectsParameter - needs to have at least one project set.
	 * @return
	 */
	public SonarApiResponse createBranch(String application, String branch, ApplicationProjectsParameter applicationProjectsParameter) {
		if (StringUtils.isEmpty(application)) {
			throw new SonarQubeUnexpectedException("Application cannot be empty");
		}
		if (applicationProjectsParameter == null) {
			throw new SonarQubeUnexpectedException("applicationProjectsParameter is required");
		}
		if (applicationProjectsParameter.areProjectsEmpty()) {
			throw new SonarQubeUnexpectedException("Need at least one project");
		}

		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new NameValuePair("application", application));
		if (StringUtils.isNotBlank(branch)) {
			parameters.add(new NameValuePair("branch", StringUtils.left(branch, 255)));
		}

		applicationProjectsParameter.getProjectsList().stream()
				.forEach(project -> parameters.add(new NameValuePair("project", project)));

		applicationProjectsParameter.getProjectBranchesList().stream()
				.forEach(projectBranch -> parameters.add(new NameValuePair("projectBranch", projectBranch)));

		return doPostWithErrorHandling(ADD_PROJECT, parameters, SonarApiResponse.class);
	}

	/**
	 * Delete an application definition.
	 * Requires 'Administrator' permission on the application
	 * @param application Application key
	 * @return If call was successful
	 */
	public SonarApiResponse deleteApplication(String application) {
		if (StringUtils.isBlank(application)) {
			throw new SonarQubeUnexpectedException("Application cannot be empty");
		}

		List<NameValuePair> parameters = NameValuePair.listOf("application", application);
		return doPostWithErrorHandling(DELETE_APPLICATION, parameters, SonarApiResponse.class);
	}

	/**
	 * Delete a branch on a given application.
	 * Requires 'Administrator' permission on the application
	 * @param application Application key
	 * @param branch Branch name
	 * @return If call was successful
	 */
	public boolean deleteBranch(String application, String branch) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Remove a project from an application
	 * Requires 'Administrator' permission on the application
	 * @param application Key of the application
	 * @param project Key of the project
	 * @return If call was successful
	 */
	public boolean removeProject(String application, String project) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Set tags on a application.
	 * Requires the following permission: 'Administer' rights on the specified application
	 * @param application Application key
	 * @param tags Comma-separated list of tags
	 * @return If call was successful
	 */
	public boolean setTags(String application, String tags) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Returns an application and its associated projects.
	 * Requires the 'Browse' permission on the application and on its child projects.
	 * @param application Application key
	 * @param branch Branch name
	 */
	public ApplicationResponse getApplication(String application, String branch) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Update an application.
	 * Requires 'Administrator' permission on the application
	 * @param application Application key
	 * @param name New name for the application
	 * @param description New description for the application (optional)
	 */
	public boolean updateApplication(String application, String name, String description) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Update a branch on a given application.
	 * Requires 'Administrator' permission on the application and 'Browse' permission on its child projects
	 * @param application Application key
	 * @param project
	 * @param projectBranch
	 * @param branch
	 * @param name
	 */
	public boolean updateBranch(String application, List<String> projects, List<String> projectBranch, String branch, String name) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

}