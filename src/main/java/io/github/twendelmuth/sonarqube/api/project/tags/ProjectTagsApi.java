package io.github.twendelmuth.sonarqube.api.project.tags;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import io.github.twendelmuth.sonarqube.api.NameValuePair;
import io.github.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import io.github.twendelmuth.sonarqube.api.SonarQubeServer;
import io.github.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ProjectTagsApi extends AbstractApiEndPoint {
	private static final String SET_TAGS = "/api/project_tags/set";

	public ProjectTagsApi(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super(server, jsonMapper, logger);
	}

	/**
	 * Set tags on a project.
	 * Requires the following permission: 'Administer' rights on the specified project
	 * Will <strong>overwrite</strong> any existing tags.
	 * 
	 * @param projectKey which project should be updated?
	 * @param tags Collection with all the tags
	 */
	public SonarApiResponse setTags(String projectKey, Collection<String> tags) {
		return setTags(projectKey, StringUtils.join(tags, ","));
	}

	/**
	 * Set tags on a project.
	 * Requires the following permission: 'Administer' rights on the specified project
	 * Will <strong>overwrite</strong> any existing tags.
	 * 
	 * @param projectKey which project should be updated?
	 * @param tags comma separated set of tags.
	 */
	public SonarApiResponse setTags(String projectKey, String tags) {
		List<NameValuePair> parameters = NameValuePair.listOf("project", projectKey, "tags", tags);
		return doPostWithErrorHandling(SET_TAGS, parameters, SonarApiResponse.class);
	}

}
