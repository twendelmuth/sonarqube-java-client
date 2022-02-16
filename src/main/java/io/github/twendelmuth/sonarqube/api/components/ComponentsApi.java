package io.github.twendelmuth.sonarqube.api.components;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.github.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import io.github.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import io.github.twendelmuth.sonarqube.api.SonarQubeServer;
import io.github.twendelmuth.sonarqube.api.components.response.SearchProjectResponse;
import io.github.twendelmuth.sonarqube.api.exception.SonarQubeClientException;
import io.github.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import io.github.twendelmuth.sonarqube.api.response.Component;
import io.github.twendelmuth.sonarqube.api.response.Paging;

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
			pageSize = sanitizePageSize(pageSize);
			page = sanitizePageNumber(page);

			String parameters = String.format("?p=%d&ps=%d", page, pageSize);

			if (searchKey != null) {
				searchKey = urlEscapeSearchKey(searchKey);
				parameters += String.format("&filter=%s", searchKey);
			}
			return doGetWithErrorHandling(SEARCH_PROJECTS_ENDPOINT + parameters, SearchProjectResponse.class);
		} catch (UnsupportedEncodingException e) {
			throw new SonarQubeClientException("Had issues finding UTF-8 encoding?", e);
		}
	}

	private static final int PAGING_SIZE = 100;

	/**
	 * Returns all {@link Component} by paging through the SonarQube result set.
	 * 
	 * @param searchKey
	 * @return
	 */
	public List<Component> searchProjects(String searchKey) {
		List<Component> allComponentsList = new ArrayList<>();

		SearchProjectResponse initialResponse = searchProjects(searchKey, PAGING_SIZE, 1);
		allComponentsList.addAll(initialResponse.getComponents());
		Paging pagingInformation = initialResponse.getPaging();
		int amountOfPages = pagingInformation.getTotal() / pagingInformation.getPageSize();
		if (pagingInformation.getTotal() % pagingInformation.getPageSize() > 0) {
			amountOfPages++;
		}
		if (amountOfPages > 1) {
			for (int page = 2; page <= amountOfPages; page++) {
				allComponentsList.addAll(searchProjects(searchKey, PAGING_SIZE, page).getComponents());
			}
		}

		return allComponentsList;
	}

	private String urlEscapeSearchKey(String searchKey) throws UnsupportedEncodingException {
		searchKey = URLEncoder.encode("query = \"" + searchKey + "\"", StandardCharsets.UTF_8.displayName());
		searchKey = searchKey.replace("+", "%20");
		return searchKey;
	}

	private int sanitizePageNumber(int page) {
		if (page < 1) {
			page = 1;
		}
		return page;
	}

	private int sanitizePageSize(int pageSize) {
		if (pageSize > 500) {
			pageSize = 500;
		} else
			pageSize = sanitizePageNumber(pageSize);
		return pageSize;
	}

}
