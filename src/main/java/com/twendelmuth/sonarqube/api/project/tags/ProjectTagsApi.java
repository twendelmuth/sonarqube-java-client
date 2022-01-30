package com.twendelmuth.sonarqube.api.project.tags;

import java.util.List;

import com.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import com.twendelmuth.sonarqube.api.NameValuePair;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

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
	 * @param tags Set with all the tags
	 */
	public SonarApiResponse setTags(String projectKey, List<String> tags) {
		StringBuilder tagBuilder = new StringBuilder();
		tags.forEach(tag -> {
			if (tagBuilder.length() > 0) {
				tagBuilder.append(",");
			}
			tagBuilder.append(tag);
		});
		return setTags(projectKey, tagBuilder.toString());
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
