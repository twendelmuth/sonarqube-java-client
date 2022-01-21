package com.twendelmuth.sonarqube.api.components;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.components.response.SearchProjectResponse;
import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;
import com.twendelmuth.sonarqube.testing.util.UrlTools;

class ComponentsApiTest extends AbstractApiEndPointTest<ComponentsApi> {

	@Override
	protected ComponentsApi buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new ComponentsApi(sonarQubeServer, jsonMapper, testLogger);
	}

	@Test
	void testSearchProjects() {
		ComponentsApi componentsApi = buildClassUnderTest(getStringFromResource("search_projects.json"));

		SearchProjectResponse response = componentsApi.searchProjects("something", 100, 1);
		assertNotNull(response);
		assertEquals(3, response.getPaging().getTotal());

		assertEquals(3, response.getComponents().size());
		assertEquals("my_project", response.getComponents().get(0).getKey());
		assertEquals(2, response.getComponents().get(0).getTags().size());
	}

	@Test
	void testServerException() {
		ComponentsApi componentsApi = buildClassUnderTest(new SonarQubeServerError("Error", 403, "some body"));

		SearchProjectResponse response = componentsApi.searchProjects("something", 100, 1);
		assertNotNull(response);
		assertEquals(403, response.getStatusCode());
	}

	@Test
	void testSearchProjects_jsonIssues() {
		ComponentsApi componentsApi = buildClassUnderTest(getStringFromResource("search_projects.json"),
				new SonarQubeClientJsonException("Something happened", null));

		SearchProjectResponse response = componentsApi.searchProjects("something", 100, 1);
		assertNotNull(response);
		assertEquals(200, response.getStatusCode());
		assertEquals(1, getTestLogger().countErrorMessages(), "Expected to have one log warning!");
	}

	@Test
	void testSearchProjects_urlEncoding() throws Exception {
		ComponentsApi componentsApi = buildClassUnderTest(getStringFromResource("search_projects.json"));

		ArgumentCaptor<String> endpointParameter = ArgumentCaptor.forClass(String.class);
		componentsApi.searchProjects("something", 100, 1);

		Mockito.verify(getSonarQubeServer()).doGet(endpointParameter.capture());
		String filterParameter = extractFilterFromQuery(endpointParameter.getValue());
		assertEquals("query%20%3D%20%22something%22", filterParameter);
	}

	@Test
	void testSearchProjects_noKey() throws Exception {
		ComponentsApi componentsApi = buildClassUnderTest(getStringFromResource("search_projects.json"));

		ArgumentCaptor<String> endpointParameter = ArgumentCaptor.forClass(String.class);
		componentsApi.searchProjects(null, 100, 1);

		Mockito.verify(getSonarQubeServer()).doGet(endpointParameter.capture());
		String endPoint = endpointParameter.getValue();
		assertTrue(StringUtils.isBlank(extractFilterFromQuery(endPoint)));
	}

	@Test
	void testSearchProjects_pageSizeOver500() {
		testSearchProjectsPageSize(501, 500);
	}

	@Test
	void testSearchProjects_pageSize500() throws Exception {
		testSearchProjectsPageSize(500, 500);
	}

	@Test
	void testSearchProjects_pageSize0() throws Exception {
		testSearchProjectsPageSize(0, 1);
	}

	@Test
	void testSearchProjects_pageNumber0() throws Exception {
		testSearchProjectPageNumber(0, 1);
	}

	@Test
	void testSearchProjects_pageNumber() throws Exception {
		testSearchProjectPageNumber(1, 1);
	}

	private void testSearchProjectsPageSize(int pageSize, int expectedPageSize) {
		ComponentsApi componentsApi = buildClassUnderTest(getStringFromResource("search_projects.json"));

		ArgumentCaptor<String> endpointParameter = ArgumentCaptor.forClass(String.class);
		componentsApi.searchProjects("", pageSize, 1);

		try {
			Mockito.verify(getSonarQubeServer()).doGet(endpointParameter.capture());
		} catch (SonarQubeServerError e) {
		}
		String endPoint = endpointParameter.getValue();
		assertEquals(expectedPageSize, extractPageSizeFromQuery(endPoint));
	}

	private void testSearchProjectPageNumber(int pageNumber, int expectedPageNumber) {
		ComponentsApi componentsApi = buildClassUnderTest(getStringFromResource("search_projects.json"));

		ArgumentCaptor<String> endpointParameter = ArgumentCaptor.forClass(String.class);
		componentsApi.searchProjects("", 100, pageNumber);

		try {
			Mockito.verify(getSonarQubeServer()).doGet(endpointParameter.capture());
		} catch (SonarQubeServerError e) {
		}
		String endPoint = endpointParameter.getValue();
		assertEquals(expectedPageNumber, extractPageNumberFromQuery(endPoint));
	}

	private int extractPageSizeFromQuery(String query) {
		String pageSizeParameter = UrlTools.extractQueryParameterMap(query).get("ps");
		if (StringUtils.isNumeric(pageSizeParameter)) {
			return Integer.valueOf(pageSizeParameter);
		}
		return 0;
	}

	private int extractPageNumberFromQuery(String query) {
		String pageNumberParameter = UrlTools.extractQueryParameterMap(query).get("p");
		if (StringUtils.isNumeric(pageNumberParameter)) {
			return Integer.valueOf(pageNumberParameter);
		}
		return 0;
	}

	private String extractFilterFromQuery(String query) {
		return UrlTools.extractQueryParameterMap(query).get("filter");
	}

}
