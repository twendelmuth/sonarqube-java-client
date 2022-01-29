package com.twendelmuth.sonarqube.api.it.create;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
import com.twendelmuth.sonarqube.api.ce.response.TaskResponse;
import com.twendelmuth.sonarqube.api.components.ComponentsApi;
import com.twendelmuth.sonarqube.api.components.response.SearchProjectResponse;
import com.twendelmuth.sonarqube.api.it.docker.SonarQubeDockerContainer;
import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.project.tags.ProjectTagsApi;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

@Tag("IntegrationTest")
class SonarQubeClientIntegrationTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SonarQubeClientIntegrationTest.class);

	private SonarQubeClient client;

	private final String projectKey = "my-project";

	@BeforeAll
	static void startAllSonarQubeServers() {
		Arrays.asList(SonarQubeVersion.values()).stream()
				.forEach(version -> {
					StopWatch bootClock = StopWatch.createStarted();
					SonarQubeDockerContainer.build(version).startSonarQubeContainer();
					bootClock.stop();
					LOGGER.info("Booted up SonarQube Server {} in {} ms", version.name(), bootClock.getTime(TimeUnit.MILLISECONDS));
				});
	}

	@AfterAll
	static void shutDownAllSonarQubeServers() {
		Arrays.asList(SonarQubeVersion.values()).stream()
				.forEach(version -> {
					SonarQubeDockerContainer.build(version).stopSonarQubeContainer();
				});
	}

	@AfterEach
	void teardown() {
		deleteProject(client, projectKey);
		client = null;
	}

	private void createProjectAndAssertSuccess(SonarQubeClient client, String key) {
		client.projectsApi().create(key, key);
	}

	private void deleteProject(SonarQubeClient client, String key) {
		client.projectsApi().delete(key);
	}

	private void assertSearchResponse(SearchProjectResponse searchResponse, int statusCode, int totalResults, String projectKey) {
		Assertions.assertAll(
				() -> assertNotNull(searchResponse),
				() -> assertEquals(200, searchResponse.getStatusCode()),
				() -> assertEquals("OriginalBody: " + searchResponse.getReturnedBody(), 1, searchResponse.getPaging().getTotal()),
				() -> assertEquals(projectKey, searchResponse.getComponents().get(0).getKey()));
	}

	private SonarQubeClient createClient(SonarQubeVersion version) {
		SonarQubeDockerContainer container = SonarQubeDockerContainer.build(version);
		container.startSonarQubeContainer();
		SonarQubeClient client = new SonarQubeClient(container.getServerConnectionString(), container.getApiToken());
		createProjectAndAssertSuccess(client, projectKey);
		return client;

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
		ActivityResponse response = client.computeEngine().getActivities(ActivitiesParameter.builder().build());
		assertEquals("Expected status 200, body: " + response.getReturnedBody(), 200, response.getStatusCode());
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

		assertEquals("Expected status 200, body: " + response.getReturnedBody(), 200, response.getStatusCode());
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

		assertEquals("Expected status 200, body: " + response.getReturnedBody(), 200, response.getStatusCode());
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

		TaskResponse response = client.computeEngine().getTask("something", TaskAdditionalField.SCANNERCONTEXT, TaskAdditionalField.STACKTRACE,
				TaskAdditionalField.WARNINGS);
		assertNull(response.getTask());
	}

}
