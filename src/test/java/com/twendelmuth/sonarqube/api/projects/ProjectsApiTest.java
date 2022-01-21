package com.twendelmuth.sonarqube.api.projects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import com.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;

class ProjectsApiTest extends AbstractApiEndPointTest<ProjectsApi> {

	@Override
	protected ProjectsApi buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new ProjectsApi(sonarQubeServer, jsonMapper, testLogger);
	}

	@Test
	void createProject() {
		ProjectsApi projectsApi = buildClassUnderTest(204, getStringFromResource("createProject.json"));
		assertTrue(projectsApi.create("project", "project"));
	}

	@Test
	void createProject_failed() {
		ProjectsApi projectsApi = buildClassUnderTest(new SonarQubeServerError("error", 403, "Not authed"));
		assertFalse(projectsApi.create("project", "project"));
	}

	@Test
	void deleteProject() {
		ProjectsApi projectsApi = buildClassUnderTest(204, null);
		assertTrue(projectsApi.delete("project"));
	}

}
