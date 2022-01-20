package com.twendelmuth.sonarqube.api.ce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.ce.response.ActivityResponse;
import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;

class ComputeEngineApiTest extends AbstractApiEndPointTest<ComputeEngineApi> {
	@Override
	protected ComputeEngineApi buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new ComputeEngineApi(sonarQubeServer, jsonMapper, testLogger);
	}

	@Test
	void testActivityResult() {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(getStringFromResource("activity_result.json"));

		ActivityResponse response = computeEngineApi.getActivities(1000);
		assertEquals(2, response.getTasks().size());
		assertEquals("project_1", response.getTasks().get(0).getComponentKey());
		assertEquals("project_2", response.getTasks().get(1).getComponentKey());
		assertEquals(0, getTestLogger().countErrorMessages(), "No error messages expected");
	}

	@Test
	void testActivityResult_notAuthed() {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(new SonarQubeServerError("Forbidden", 403, "Please provide API Token!"));

		ActivityResponse response = computeEngineApi.getActivities(1000);
		assertNotNull(response);
		assertEquals(403, response.getStatusCode());
		assertEquals(1, getTestLogger().countErrorMessages(), "Expected to have one log warning!");
	}

	@Test
	void testActivityResult_jsonIssues() {
		ComputeEngineApi computeEngineApi = buildClassUnderTest(getStringFromResource("activity_result.json"),
				new SonarQubeClientJsonException("Something happened", null));

		ActivityResponse response = computeEngineApi.getActivities(1000);
		assertNotNull(response);
		assertEquals(200, response.getStatusCode());
		assertEquals(1, getTestLogger().countErrorMessages(), "Expected to have one log warning!");
	}

}
