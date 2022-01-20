package com.twendelmuth.sonarqube.api.components;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.components.response.SearchProjectResponse;
import com.twendelmuth.sonarqube.api.exception.SonarQubeClientException;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;

public class ComponentsApi extends AbstractApiEndPoint {

	private static final String SEARCH_PROJECTS_ENDPOINT = "/api/components/search_projects";

	public ComponentsApi(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super(server, jsonMapper, logger);
	}

	/**
	 * Search for projects
	
	 * @param searchKey Will be used as `filter='query = "<searchKey>"'.`
	 * @param pageSize Page size. Must be greater than 0 and less or equal than 500
	 * @param page 1-based page number
	 * @return
	 */
	public SearchProjectResponse searchProjects(String searchKey, int pageSize, int page) {
		try {
			searchKey = URLEncoder.encode("query = \"" + searchKey + "\"", StandardCharsets.UTF_8.displayName());
			searchKey = searchKey.replace("+", "%20");
			String parameters = String.format("?p=%d&ps=%d&filter=%s", page, pageSize, searchKey);
			return doGetWithErrorHandling(SEARCH_PROJECTS_ENDPOINT + parameters, SearchProjectResponse.class);
		} catch (UnsupportedEncodingException e) {
			throw new SonarQubeClientException("Had issues finding UTF-8 encoding?", e);
		}
	}

}
