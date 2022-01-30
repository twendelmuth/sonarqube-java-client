package com.twendelmuth.sonarqube.api.it.create;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twendelmuth.sonarqube.api.SonarQubeClient;
import com.twendelmuth.sonarqube.api.ce.ActivitiesParameter;
import com.twendelmuth.sonarqube.api.ce.ActivitiesParameter.ActivitiesStatus;
import com.twendelmuth.sonarqube.api.ce.ActivitiesParameter.ActivitiesType;
import com.twendelmuth.sonarqube.api.ce.ComputeEngineApi;
import com.twendelmuth.sonarqube.api.ce.TaskAdditionalField;
import com.twendelmuth.sonarqube.api.ce.response.ActivityResponse;
import com.twendelmuth.sonarqube.api.ce.response.ActivityStatusResponse;
import com.twendelmuth.sonarqube.api.ce.response.ComponentResponse;
import com.twendelmuth.sonarqube.api.ce.response.TaskResponse;
import com.twendelmuth.sonarqube.api.components.ComponentsApi;
import com.twendelmuth.sonarqube.api.components.response.SearchProjectResponse;
import com.twendelmuth.sonarqube.api.it.AbstractSonarQubeIntegrationTest;
import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.project.tags.ProjectTagsApi;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

@Tag("IntegrationTest")
class SonarQubeClientIntegrationTest extends AbstractSonarQubeIntegrationTest {

	public static final Logger LOGGER = LoggerFactory.getLogger(SonarQubeClientIntegrationTest.class);

	private SonarQubeClient client;

	public final String projectKey = "my-project";

	private void assertSearchResponse(SearchProjectResponse searchResponse, int statusCode, int totalResults, String projectKey) {
		Assertions.assertAll(
				() -> assertNotNull(searchResponse),
				() -> assertEquals(200, searchResponse.getStatusCode()),
				() -> assertEquals(1, searchResponse.getPaging().getTotal(), "OriginalBody: " + searchResponse.getReturnedBody()),
				() -> assertEquals(projectKey, searchResponse.getComponents().get(0).getKey()));
	}

	/**
	 * Integration test, executing:
	 * 
	 * {@link ComponentsApi#searchProjects(String, int, int)}
	 */
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void searchProjects(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);
		SearchProjectResponse searchResponse = client.componentsApi().searchProjects(projectKey, 1, 1);
		assertSearchResponse(searchResponse, 200, 1, projectKey);
	}

	/**
	 * Integration test, executing:
	 * 
	 * {@link ComponentsApi#searchProjects(String, int, int)}
	 */
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void searchProjects_pageSizeOver1000(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);
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
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void updateTags(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);
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
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void updateTags_duplication(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);
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
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void updateTags_tagTrimming(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);
		SonarApiResponse setTagsResponse = client.projectTagsApi().setTags(projectKey, "tag1 , tag2 , tag1,tag2");
		assertEquals(204, setTagsResponse.getStatusCode());

		SearchProjectResponse searchResponse = client.componentsApi().searchProjects(projectKey, 1, 1);
		assertSearchResponse(searchResponse, 200, 1, projectKey);
		Assertions.assertAll(
				() -> assertEquals(2, searchResponse.getComponents().get(0).getTags().size()),
				() -> assertEquals("tag1", searchResponse.getComponents().get(0).getTags().get(0)),
				() -> assertEquals("tag2", searchResponse.getComponents().get(0).getTags().get(1)));
	}

	/**
	 * Check that SonarQube accepts our implementation of getActivities without any parameters
	 * 
	 * {@link ComputeEngineApi#getActivities(ActivitiesParameter)}
	 */
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void activities_noParams(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);
		ActivityResponse response = client.computeEngine().getActivities(ActivitiesParameter.builder().build());
		assertEquals(200, response.getStatusCode(), "Expected status 200, body: " + response.getReturnedBody());
	}

	/**
	 * Check that SonarQube accepts our implementation of getActivities with all parameters (no query param)
	 * 
	 * {@link ComputeEngineApi#getActivities(ActivitiesParameter)}
	 */
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void activities_allParamsWithComponent(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);
		ZonedDateTime time = ZonedDateTime.ofInstant(LocalDateTime.of(2022, Month.JANUARY, 1, 1, 1, 1, 0), ZoneOffset.UTC, ZoneId.of("Z"));

		ActivityResponse response = client.computeEngine().getActivities(ActivitiesParameter.builder()
				.component(projectKey)
				.maxExecutedAt(time)
				.minSubmittedAt(time)
				.onlyCurrents(false)
				.pageSize(30)
				.addStatus(ActivitiesStatus.SUCCESS)
				.addStatus(ActivitiesStatus.FAILED)
				.addStatus(ActivitiesStatus.CANCELED)
				.addStatus(ActivitiesStatus.PENDING)
				.addStatus(ActivitiesStatus.IN_PROGRESS)
				.type(ActivitiesType.REPORT)
				.build());

		assertEquals(200, response.getStatusCode(), "Expected status 200, body: " + response.getReturnedBody());
	}

	/**
	 * Check that SonarQube accepts our implementation of getActivities with all parameters (no components param)
	 * 
	 * {@link ComputeEngineApi#getActivities(ActivitiesParameter)}
	 */
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void activities_allParamsWithApache(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);
		ZonedDateTime time = ZonedDateTime.ofInstant(LocalDateTime.of(2022, Month.JANUARY, 1, 1, 1, 1, 0), ZoneOffset.UTC, ZoneId.of("Z"));

		ActivityResponse response = client.computeEngine().getActivities(ActivitiesParameter.builder()
				.query("Apache")
				.maxExecutedAt(time)
				.minSubmittedAt(time)
				.onlyCurrents(false)
				.pageSize(30)
				.addStatus(ActivitiesStatus.SUCCESS)
				.addStatus(ActivitiesStatus.FAILED)
				.addStatus(ActivitiesStatus.CANCELED)
				.addStatus(ActivitiesStatus.PENDING)
				.addStatus(ActivitiesStatus.IN_PROGRESS)
				.type(ActivitiesType.REPORT)
				.build());

		assertEquals(200, response.getStatusCode(), "Expected status 200, body: " + response.getReturnedBody());
	}

	/**
	 * Check that {@link ComputeEngineApi#getTask(String, com.twendelmuth.sonarqube.api.ce.TaskAdditionalField...)} works.
	 * Will return nothing because we can't trigger tasks on frehsly booted-up SonarQube instances.
	 * 
	 */
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void getTask(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);

		TaskResponse response = client.computeEngine().getTask("something", TaskAdditionalField.SCANNERCONTEXT, TaskAdditionalField.STACKTRACE,
				TaskAdditionalField.WARNINGS);
		assertNull(response.getTask());
	}

	private void assertActivityStatusResponse(ActivityStatusResponse response) {
		assertAll(
				() -> assertEquals(0, response.getPending()),
				() -> assertEquals(0, response.getInProgress()),
				() -> assertEquals(0, response.getFailing()),
				() -> assertEquals(0L, response.getPendingTime()));
	}

	/**
	 * Check that {@link ComputeEngineApi#getActivityStatus()} returns something.
	 * Since the server has done nothing at this point, we should get 0 as replies.
	 */
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void getActivityStatus(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);

		ActivityStatusResponse response = client.computeEngine().getActivityStatus();
		assertActivityStatusResponse(response);
	}

	/**
	 * Check that {@link ComputeEngineApi#getActivityStatus(String)} returns something.
	 * Since the server has done nothing at this point, we should get 0 as replies.
	 */
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void getActivityStatusWithParameter(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);

		ActivityStatusResponse response = client.computeEngine().getActivityStatus("my-component");
		assertActivityStatusResponse(response);
	}

	/**
	 * Check that {@link ComputeEngineApi#getComponent(String)} call is correct.
	 * Will not return any data since nothing is on that server.
	 */
	@ParameterizedTest
	@EnumSource(SonarQubeVersion.class)
	void getComponent(SonarQubeVersion version) {
		client = createClient(version);
		createProject(client, projectKey);

		ComponentResponse response = client.computeEngine().getComponent("component");
		assertNotNull(response);
		assertNotNull(response.getQueue());
		assertNull(response.getCurrent());
		assertEquals(0, response.getQueue().size());
	}

}
