package com.twendelmuth.sonarqube.api.ce;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.ce.response.ActivityResponse;
import com.twendelmuth.sonarqube.api.ce.response.Task;
import com.twendelmuth.sonarqube.api.ce.response.TaskResponse;
import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;
import com.twendelmuth.sonarqube.testing.util.UrlTools;

class ComputeEngineApiTest extends AbstractApiEndPointTest<ComputeEngineApi> {
	@Override
	protected ComputeEngineApi buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new ComputeEngineApi(sonarQubeServer, jsonMapper, testLogger);
	}

	@Test
	void testActivityResult() {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(getStringFromResource("activity_result.json"));

		ActivityResponse response = computeEngineApi.getActivities(ActivitiesParameter.builder().build());
		assertEquals(0, getTestLogger().countErrorMessages(), "No error messages expected");
		assertEquals(2, response.getTasks().size());
		assertEquals("project_1", response.getTasks().get(0).getComponentKey());
		assertEquals("project_2", response.getTasks().get(1).getComponentKey());
		assertEquals(0, getTestLogger().countErrorMessages(), "No error messages expected");

		Task task1 = response.getTasks().get(0);
		ZoneOffset zoneOffset = ZoneOffset.of("Z");
		ZoneId zoneId = ZoneId.of("UTC");
		ZonedDateTime submittedAt = ZonedDateTime.ofInstant(LocalDateTime.of(2015, Month.AUGUST, 13, 21, 34, 59), zoneOffset, zoneId);
		ZonedDateTime startedAt = ZonedDateTime.ofInstant(LocalDateTime.of(2015, Month.AUGUST, 13, 21, 35, 0), zoneOffset, zoneId);
		ZonedDateTime executedAt = ZonedDateTime.ofInstant(LocalDateTime.of(2015, Month.AUGUST, 13, 21, 35, 10), zoneOffset, zoneId);

		assertAll(
				() -> assertEquals("my-org-1", task1.getOrganization()),
				() -> assertEquals("BU_dO1vsORa8_beWCwsP", task1.getId()),
				() -> assertEquals("REPORT", task1.getType()),
				() -> assertEquals("AU-Tpxb--iU5OvuD2FLy", task1.getComponentId()),
				() -> assertEquals("Project One", task1.getComponentName()),
				() -> assertEquals("TRK", task1.getComponentQualifier()),
				() -> assertEquals("AU-TpxcB-iU5Ovu12345", task1.getAnalysisId()),
				() -> assertEquals("SUCCESS", task1.getStatus()),
				() -> assertEquals("john", task1.getSubmitterLogin()),
				() -> assertEquals(10000L, task1.getExecutionTimeMs()),
				() -> assertFalse(task1.isLogs()),
				() -> assertFalse(task1.isHasErrorStacktrace()),
				() -> assertTrue(task1.isHasScannerContext()),
				() -> assertEquals(submittedAt, task1.getSubmittedAt()),
				() -> assertEquals(startedAt, task1.getStartedAt()),
				() -> assertEquals(executedAt, task1.getExecutedAt()));

	}

	@Test
	void testActivityResult_parameterNullSafety() {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(getStringFromResource("activity_result.json"));

		ActivityResponse response = computeEngineApi.getActivities(null);
		assertEquals(0, getTestLogger().countErrorMessages(), "No error messages expected");
		assertEquals(2, response.getTasks().size());
		assertEquals("project_1", response.getTasks().get(0).getComponentKey());
		assertEquals("project_2", response.getTasks().get(1).getComponentKey());
	}

	@Test
	void testActivityResult_notAuthed() {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(new SonarQubeServerError("Forbidden", 403, "Please provide API Token!"));

		ActivityResponse response = computeEngineApi.getActivities(ActivitiesParameter.builder().build());
		assertNotNull(response);
		assertEquals(403, response.getStatusCode());
		assertEquals(1, getTestLogger().countErrorMessages(), "Expected to have one log warning!");
	}

	@Test
	void testActivityResult_jsonIssues() {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(getStringFromResource("activity_result.json"),
				new SonarQubeClientJsonException("Something happened", null));

		ActivityResponse response = computeEngineApi.getActivities(ActivitiesParameter.builder().build());
		assertNotNull(response);
		assertEquals(200, response.getStatusCode());
		assertEquals(1, getTestLogger().countErrorMessages(), "Expected to have one log warning!");
	}

	@Test
	void testTaskResponse() throws Exception {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(getStringFromResource("task_result.json"));
		TaskResponse taskResponse = computeEngineApi.getTask("AVAn5RKqYwETbXvgas-I");
		assertNotNull(taskResponse.getTask());

		ArgumentCaptor<String> endpointParameter = ArgumentCaptor.forClass(String.class);
		Mockito.verify(getSonarQubeServer()).doGet(endpointParameter.capture());
		Map<String, String> parameterMap = UrlTools.extractQueryParameterMap(endpointParameter.getValue());

		assertEquals("AVAn5RKqYwETbXvgas-I", parameterMap.get("id"));
		assertNull(parameterMap.get("additionalFields"));
	}

	@Test
	void testTaskResponse_oneParameter() throws Exception {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(getStringFromResource("task_result.json"));
		TaskResponse taskResponse = computeEngineApi.getTask("AVAn5RKqYwETbXvgas-I", TaskAdditionalField.STACKTRACE);
		assertNotNull(taskResponse.getTask());

		ArgumentCaptor<String> endpointParameter = ArgumentCaptor.forClass(String.class);
		Mockito.verify(getSonarQubeServer()).doGet(endpointParameter.capture());
		Map<String, String> parameterMap = UrlTools.extractQueryParameterMap(endpointParameter.getValue());

		assertEquals("stacktrace", parameterMap.get("additionalFields"));
	}

	@Test
	void testTaskResponse_allParameters() throws Exception {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(getStringFromResource("task_result.json"));
		TaskResponse taskResponse = computeEngineApi.getTask("AVAn5RKqYwETbXvgas-I", TaskAdditionalField.STACKTRACE,
				TaskAdditionalField.SCANNERCONTEXT, TaskAdditionalField.WARNINGS);
		assertNotNull(taskResponse.getTask());

		ArgumentCaptor<String> endpointParameter = ArgumentCaptor.forClass(String.class);
		Mockito.verify(getSonarQubeServer()).doGet(endpointParameter.capture());
		Map<String, String> parameterMap = UrlTools.extractQueryParameterMap(endpointParameter.getValue());

		assertEquals("stacktrace,scannerContext,warnings", parameterMap.get("additionalFields"));
	}

}
