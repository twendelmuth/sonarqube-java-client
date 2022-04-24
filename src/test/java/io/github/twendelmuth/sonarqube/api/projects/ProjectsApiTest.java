package io.github.twendelmuth.sonarqube.api.projects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import io.github.twendelmuth.sonarqube.api.IOHelper;
import io.github.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import io.github.twendelmuth.sonarqube.api.SonarQubeServer;
import io.github.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import io.github.twendelmuth.sonarqube.api.exception.SonarQubeValidationException;
import io.github.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;
import io.github.twendelmuth.sonarqube.api.projects.response.Project;
import io.github.twendelmuth.sonarqube.api.projects.response.ProjectResponse;
import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ProjectsApiTest extends AbstractApiEndPointTest<ProjectsApi> {
	@Override
	protected ProjectsApi buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new ProjectsApi(sonarQubeServer, jsonMapper, testLogger);
	}

	public static String getCreateProjectJson() {
		return IOHelper.getStringFromResource(ProjectsApiTest.class, "createProject.json");
	}

	public static String getCreateProjectExistsJson() {
		return IOHelper.getStringFromResource(ProjectsApiTest.class, "createProjectExists.json");
	}

	@Test
	void createProject() {
		ProjectsApi projectsApi = buildClassUnderTest(204, getCreateProjectJson());
		ProjectResponse response = projectsApi.create("project-name", "project-key");
		assertTrue(response.isSuccess());
		assertEquals("/api/projects/create", getEndpointFromPostRequest());
		Project project = response.getProject();
		assertAll(
				() -> assertNotNull(project),
				() -> assertEquals("project-key", project.getKey()),
				() -> assertEquals("project-name", project.getName()),
				() -> assertEquals("public", project.getVisibility()));

	}

	@Test
	void createProjectExists() {
		ProjectsApi projectsApi = buildClassUnderTest(400, getCreateProjectExistsJson());
		ProjectResponse response = projectsApi.create("project-name", "project-key");
		assertFalse(response.isSuccess());

		assertAll(
				() -> assertEquals(1, response.getErrors().size()),
				() -> assertEquals("Could not create Project, key already exists: project-key", response.getErrors().get(0).getMsg()));
	}

	@Test
	void createProject_failed() {
		ProjectsApi projectsApi = buildClassUnderTest(new SonarQubeServerError("error", 403, "{}"));
		assertFalse(projectsApi.create("project", "project").isSuccess());
	}

	@Test
	void deleteProject() {
		ProjectsApi projectsApi = buildClassUnderTest(204, null);
		SonarApiResponse response = projectsApi.delete("project");
		assertEquals("/api/projects/delete", getEndpointFromPostRequest());
		assertTrue(response.isSuccess());
	}

	@Test
	void bulkDelete() {
		ProjectsApi projectsApi = buildClassUnderTest(204, null);
		ProjectFilterParameter parameters = ProjectFilterParameter.bulkDeleteProjectFilterBuilder().query("query").build();
		SonarApiResponse response = projectsApi.bulkDelete(parameters);

		assertAll(
				() -> assertTrue(response.isSuccess()),
				() -> assertEquals("/api/projects/bulk_delete", getEndpointFromPostRequest()),
				() -> assertEquals("query", getFirstParameterValue(getParameterListFromPostRequest(), "q")));
	}

	@Test
	void bulkDelete_notEnoughParameters() {
		ProjectsApi projectsApi = buildClassUnderTest(204, null);
		ProjectFilterParameter parameters = new ProjectFilterParameter();
		assertThrows(SonarQubeValidationException.class, () -> projectsApi.bulkDelete(parameters));
	}

	@Test
	void updateKey() {
		ProjectsApi projectsApi = buildClassUnderTest(200, null);

		SonarApiResponse response = projectsApi.updateKey("project1", "project2");
		assertAll(
				() -> assertTrue(response.isSuccess()),
				() -> assertEquals("/api/projects/update_key", getEndpointFromPostRequest()),
				() -> assertEquals("project1", getFirstParameterValue(getParameterListFromPostRequest(), "from")),
				() -> assertEquals("project2", getFirstParameterValue(getParameterListFromPostRequest(), "to")));
	}

	@Test
	void updateKey_fromNull() {
		ProjectsApi projectsApi = buildClassUnderTest(200, null);
		assertThrows(SonarQubeValidationException.class, () -> projectsApi.updateKey(null, "project2"));
	}

	@Test
	void updateKey_toNull() {
		ProjectsApi projectsApi = buildClassUnderTest(200, null);
		assertThrows(SonarQubeValidationException.class, () -> projectsApi.updateKey("project1", null));
	}

	@Test
	void updateVisibility() {
		ProjectsApi projectsApi = buildClassUnderTest(200, null);
		SonarApiResponse response = projectsApi.updateVisibility("project1", ProjectVisibility.PRIVATE);
		assertAll(
				() -> assertTrue(response.isSuccess()),
				() -> assertEquals("/api/projects/update_visibility", getEndpointFromPostRequest()),
				() -> assertEquals("project1", getFirstParameterValue(getParameterListFromPostRequest(), "project")),
				() -> assertEquals("private", getFirstParameterValue(getParameterListFromPostRequest(), "visibility")));
	}

	@Test
	void updateVisibility_noProject() {
		ProjectsApi projectsApi = buildClassUnderTest(200, null);
		assertThrows(SonarQubeValidationException.class, () -> projectsApi.updateVisibility(null, ProjectVisibility.PRIVATE));
	}

	@Test
	void updateVisibility_noVisibility() {
		ProjectsApi projectsApi = buildClassUnderTest(200, null);
		assertThrows(SonarQubeValidationException.class, () -> projectsApi.updateVisibility("project1", null));
	}
}
