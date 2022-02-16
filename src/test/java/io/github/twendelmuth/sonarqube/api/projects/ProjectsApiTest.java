package io.github.twendelmuth.sonarqube.api.projects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import io.github.twendelmuth.sonarqube.api.IOHelper;
import io.github.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import io.github.twendelmuth.sonarqube.api.SonarQubeServer;
import io.github.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import io.github.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;
import io.github.twendelmuth.sonarqube.api.projects.response.Project;
import io.github.twendelmuth.sonarqube.api.projects.response.ProjectResponse;

public class ProjectsApiTest extends AbstractApiEndPointTest<ProjectsApi> {

	@Override
	protected ProjectsApi buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new ProjectsApi(sonarQubeServer, jsonMapper, testLogger);
	}

	public static String getCreateProjectJson() {
		return IOHelper.getStringFromResource(ProjectsApiTest.class, "createProject.json");
	}

	@Test
	void createProject() {
		ProjectsApi projectsApi = buildClassUnderTest(204, getCreateProjectJson());
		ProjectResponse response = projectsApi.create("project-name", "project-key");
		assertTrue(response.isSuccess());
		Project project = response.getProject();
		assertAll(
				() -> assertNotNull(project),
				() -> assertEquals("project-key", project.getKey()),
				() -> assertEquals("project-name", project.getName()),
				() -> assertEquals("TRK", project.getQualifier()),
				() -> assertEquals("public", project.getVisibility()));

	}

	@Test
	void createProject_failed() {
		ProjectsApi projectsApi = buildClassUnderTest(new SonarQubeServerError("error", 403, "{}"));
		assertFalse(projectsApi.create("project", "project").isSuccess());
	}

	@Test
	void deleteProject() {
		ProjectsApi projectsApi = buildClassUnderTest(204, null);
		assertTrue(projectsApi.delete("project").isSuccess());
	}

}
