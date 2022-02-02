package com.twendelmuth.sonarqube.api.applications;

import java.util.ArrayList;
import java.util.Collection;
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
	protected static final String PROJECT_BRANCH_PARAMETER = "projectBranch";

	protected static final String BRANCH_PARAMETER = "branch";

	protected static final String VISIBILITY_PARAMETER = "visibility";

	protected static final String KEY_PARAMETER = "key";

	protected static final String DESCRIPTION_PARAMETER = "description";

	protected static final String NAME_PARAMETER = "name";

	protected static final String PROJECT_PARAMETER = "project";

	protected static final String TAGS_PARAMETER = "tags";

	private static final String APPLICATION_PARAMETER = "application";

	private static final String ADD_APPLICATION = "/api/applications/create";

	private static final String ADD_PROJECT = "/api/applications/add_project";

	private static final String DELETE_APPLICATION = "/api/applications/delete";

	private static final String DELETE_BRANCH = "/api/applications/delete_branch";

	private static final String REMOVE_PROJECT = "/api/applications/remove_project";

	private static final String SET_TAGS = "/api/applications/set_tags";

	public ApplicationsApi(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super(server, jsonMapper, logger);
	}

	private void assertProjectParameter(String project) {
		if (StringUtils.isEmpty(project)) {
			throw new SonarQubeUnexpectedException("Project cannot be empty");
		}
	}

	private void assertApplicationParameter(String application) {
		if (StringUtils.isEmpty(application)) {
			throw new SonarQubeUnexpectedException("Application cannot be empty");
		}
	}

	private void assertNameParameter(String name) {
		if (StringUtils.isBlank(name)) {
			throw new SonarQubeUnexpectedException("Name cannot be empty");
		}
	}

	private void assertBranchParameter(String branch) {
		if (StringUtils.isBlank(branch)) {
			throw new SonarQubeUnexpectedException("Branch cannot be empty");
		}
	}

	/**
	 * Add a project to an application.
	 * Requires 'Administrator' permission on the application
	 * @param application Key of the application
	 * @param project Key of the project
	 * @return If call was successful
	 */
	public SonarApiResponse addProject(String application, String project) {
		assertApplicationParameter(application);

		assertProjectParameter(project);

		List<NameValuePair> parameters = NameValuePair.listOf(APPLICATION_PARAMETER, application, PROJECT_PARAMETER, project);

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
		assertNameParameter(name);

		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new NameValuePair(NAME_PARAMETER, name));
		if (StringUtils.isNotBlank(description)) {
			parameters.add(new NameValuePair(DESCRIPTION_PARAMETER, description));
		}
		if (StringUtils.isNotBlank(key)) {
			parameters.add(new NameValuePair(KEY_PARAMETER, key));
		}
		if (visibility != null) {
			parameters.add(new NameValuePair(VISIBILITY_PARAMETER, visibility.getApiName()));
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
		assertApplicationParameter(application);
		if (applicationProjectsParameter == null) {
			throw new SonarQubeUnexpectedException("applicationProjectsParameter is required");
		}
		if (applicationProjectsParameter.areProjectsEmpty()) {
			throw new SonarQubeUnexpectedException("Need at least one project");
		}

		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new NameValuePair(APPLICATION_PARAMETER, application));
		if (StringUtils.isNotBlank(branch)) {
			parameters.add(new NameValuePair(BRANCH_PARAMETER, StringUtils.left(branch, 255)));
		}

		applicationProjectsParameter.getProjectsList().stream()
				.forEach(project -> parameters.add(new NameValuePair(PROJECT_PARAMETER, project)));

		applicationProjectsParameter.getProjectBranchesList().stream()
				.forEach(projectBranch -> parameters.add(new NameValuePair(PROJECT_BRANCH_PARAMETER, projectBranch)));

		return doPostWithErrorHandling(ADD_PROJECT, parameters, SonarApiResponse.class);
	}

	/**
	 * Delete an application definition.
	 * Requires 'Administrator' permission on the application
	 * @param application Application key
	 * @return If call was successful
	 */
	public SonarApiResponse deleteApplication(String application) {
		assertApplicationParameter(application);

		List<NameValuePair> parameters = NameValuePair.listOf(APPLICATION_PARAMETER, application);
		return doPostWithErrorHandling(DELETE_APPLICATION, parameters, SonarApiResponse.class);
	}

	/**
	 * Delete a branch on a given application.
	 * Requires 'Administrator' permission on the application
	 * @param application Application key
	 * @param branch Branch name
	 * @return If call was successful
	 */
	public SonarApiResponse deleteBranch(String application, String branch) {
		assertApplicationParameter(application);
		assertBranchParameter(branch);

		List<NameValuePair> parameters = NameValuePair.listOf(APPLICATION_PARAMETER, application, BRANCH_PARAMETER, branch);
		return doPostWithErrorHandling(DELETE_BRANCH, parameters, SonarApiResponse.class);
	}

	/**
	 * Remove a project from an application
	 * Requires 'Administrator' permission on the application
	 * @param application Key of the application
	 * @param project Key of the project
	 * @return If call was successful
	 */
	public SonarApiResponse removeProject(String application, String project) {
		assertApplicationParameter(application);
		assertProjectParameter(project);

		List<NameValuePair> parameters = NameValuePair.listOf(APPLICATION_PARAMETER, application, PROJECT_PARAMETER, project);
		return doPostWithErrorHandling(REMOVE_PROJECT, parameters, SonarApiResponse.class);
	}

	/**
	 * Set tags on a application.
	 * Requires the following permission: 'Administer' rights on the specified application
	 * @param application Application key
	 * @param tags Comma-separated list of tags
	 * @return If call was successful
	 */
	public SonarApiResponse setTags(String application, String tags) {
		assertApplicationParameter(application);

		if (tags == null) {
			tags = "";
		}

		List<NameValuePair> parameters = NameValuePair.listOf(APPLICATION_PARAMETER, application, TAGS_PARAMETER, tags);
		return doPostWithErrorHandling(SET_TAGS, parameters, SonarApiResponse.class);
	}

	public SonarApiResponse setTags(String application, Collection<String> tags) {
		return setTags(application, StringUtils.join(tags, ","));
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
