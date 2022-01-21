package com.twendelmuth.sonarqube.api.it.create;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.twendelmuth.sonarqube.api.SonarQubeClient;
import com.twendelmuth.sonarqube.api.components.ComponentsApi;
import com.twendelmuth.sonarqube.api.components.response.SearchProjectResponse;
import com.twendelmuth.sonarqube.api.it.SonarQubeClientIntegrationHelper;
import com.twendelmuth.sonarqube.api.project.tags.ProjectTagsApi;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

@Tag("IntegrationTest")
class SonarQubeClientIntegrationTest {

	private SonarQubeClient client;

	private final String projectKey = "my-project";

	@BeforeEach
	void setup() {
		client = new SonarQubeClientIntegrationHelper().getSonarQubeClient();
		createProjectAndAssertSuccess(projectKey);
	}

	@AfterEach
	void teardown() {
		deleteProject(projectKey);
	}

	private void createProjectAndAssertSuccess(String key) {
		client.projectsApi().create(key, key);
	}

	private void deleteProject(String key) {
		client.projectsApi().delete(key);
	}

	private void assertSearchResponse(SearchProjectResponse searchResponse, int statusCode, int totalResults, String projectKey) {
		Assertions.assertAll(
				() -> assertNotNull(searchResponse),
				() -> assertEquals(200, searchResponse.getStatusCode()),
				() -> assertEquals("OriginalBody: " + searchResponse.getReturnedBody(), 1, searchResponse.getPaging().getTotal()),
				() -> assertEquals(projectKey, searchResponse.getComponents().get(0).getKey()));
	}

	/**
	 * Integration test, executing:
	 * 
	 * {@link ComponentsApi#searchProjects(String, int, int)}
	 */
	@Test
	void searchProjects() {
		SearchProjectResponse searchResponse = client.componentsApi().searchProjects(projectKey, 1, 1);
		assertSearchResponse(searchResponse, 200, 1, projectKey);
	}

	/**
	 * Integration test, executing:
	 * 
	 * {@link ComponentsApi#searchProjects(String, int, int)}
	 */
	@Test
	void searchProjects_pageSizeOver1000() {
		SearchProjectResponse searchResponse = client.componentsApi().searchProjects(projectKey, 1001, 1);
		assertSearchResponse(searchResponse, 200, 1, projectKey);
	}

	/**
	 * Integration test, executing:
	 * 
	 * {@link ComponentsApi#searchProjects(String, int, int)}
	 * {@link ProjectTagsApi#setTags(String, String)}
	 * and confirms that everything has been set by calling
	 * {@link ComponentsApi#searchProjects(String, int, int)}
	 * again
	 */
	@Test
	void updateTags() {
		SearchProjectResponse searchResponse1 = client.componentsApi().searchProjects(projectKey, 1, 1);
		assertSearchResponse(searchResponse1, 200, 1, projectKey);
		assertEquals(0, searchResponse1.getComponents().get(0).getTags().size());

		SonarApiResponse setTagsResponse = client.projectTagsApi().setTags(projectKey, "tag1,tag2,tag3,tag4");
		assertEquals(204, setTagsResponse.getStatusCode());

		SearchProjectResponse searchResponse2 = client.componentsApi().searchProjects(projectKey, 1, 1);
		assertSearchResponse(searchResponse2, 200, 1, projectKey);
		Assertions.assertAll(
				() -> assertEquals(4, searchResponse2.getComponents().get(0).getTags().size()),
				() -> assertEquals("tag1", searchResponse2.getComponents().get(0).getTags().get(0)));
	}

	/**
	 * Check that SonarQube is making List of tags into a Set.
	 * 
	 * {@link ProjectTagsApi#setTags(String, String)}
	 * and confirms that everything has been set by calling
	 * {@link ComponentsApi#searchProjects(String, int, int)}y
	 * again
	 */
	@Test
	void updateTags_duplication() {
		SonarApiResponse setTagsResponse = client.projectTagsApi().setTags(projectKey, "tag1,tag1,tag1,tag4");
		assertEquals(204, setTagsResponse.getStatusCode());

		SearchProjectResponse searchResponse = client.componentsApi().searchProjects(projectKey, 1, 1);
		assertSearchResponse(searchResponse, 200, 1, projectKey);
		Assertions.assertAll(
				() -> assertEquals(2, searchResponse.getComponents().get(0).getTags().size()),
				() -> assertEquals("tag1", searchResponse.getComponents().get(0).getTags().get(0)));
	}

	/**
	 * Check that SonarQube is trimming tags
	 * 
	 * {@link ProjectTagsApi#setTags(String, String)}
	 * and confirms that everything has been set by calling
	 * {@link ComponentsApi#searchProjects(String, int, int)}y
	 * again
	 */
	@Test
	void updateTags_tagTrimming() {
		SonarApiResponse setTagsResponse = client.projectTagsApi().setTags(projectKey, "tag1 , tag2 , tag1,tag2");
		assertEquals(204, setTagsResponse.getStatusCode());

		SearchProjectResponse searchResponse = client.componentsApi().searchProjects(projectKey, 1, 1);
		assertSearchResponse(searchResponse, 200, 1, projectKey);
		Assertions.assertAll(
				() -> assertEquals(2, searchResponse.getComponents().get(0).getTags().size()),
				() -> assertEquals("tag1", searchResponse.getComponents().get(0).getTags().get(0)),
				() -> assertEquals("tag2", searchResponse.getComponents().get(0).getTags().get(1)));
	}

}
