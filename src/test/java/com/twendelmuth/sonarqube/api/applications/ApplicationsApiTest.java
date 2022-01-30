package com.twendelmuth.sonarqube.api.applications;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import com.twendelmuth.sonarqube.api.IOHelper;
import com.twendelmuth.sonarqube.api.NameValuePair;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.applications.model.Application;
import com.twendelmuth.sonarqube.api.applications.response.ApplicationResponse;
import com.twendelmuth.sonarqube.api.exception.SonarQubeUnexpectedException;
import com.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;

public class ApplicationsApiTest extends AbstractApiEndPointTest<ApplicationsApi> {

	public static String getCreateApplicationResponse() {
		return IOHelper.getStringFromResource(ApplicationsApiTest.class, "createApplication.json");
	}

	@Override
	protected ApplicationsApi buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new ApplicationsApi(sonarQubeServer, jsonMapper, testLogger);
	}

	@Test
	void addProject() {
		ApplicationsApi api = buildClassUnderTest("{}");
		assertTrue(api.addProject("my-application", "project-key").isSuccess());
	}

	@Test
	void addProject_noApplication() {
		ApplicationsApi api = buildClassUnderTest("{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.addProject("", "project-key"));
	}

	@Test
	void addProject_noProjectKey() {
		ApplicationsApi api = buildClassUnderTest("{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.addProject("my-application", ""));
	}

	@Test
	void createBranch() {
		ApplicationsApi api = buildClassUnderTest("{}");
		assertTrue(api.createBranch("my-application", "branch",
				new ApplicationProjectsParameter().addProject("my-project").addProjectBranch("projectBranch"))
				.isSuccess());
	}

	@Test
	void createBranch_noApplication() {
		ApplicationsApi api = buildClassUnderTest("{}");
		ApplicationProjectsParameter params = new ApplicationProjectsParameter().addProject("my-project");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.createBranch("", "branch", params));
	}

	@Test
	void createBranch_noProject() {
		ApplicationsApi api = buildClassUnderTest("{}");
		ApplicationProjectsParameter params = new ApplicationProjectsParameter();
		assertThrows(SonarQubeUnexpectedException.class, () -> api.createBranch("my-application", "branch", params));
	}

	@Test
	void createBranch_noApplicationParams() {
		ApplicationsApi api = buildClassUnderTest("{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.createBranch("my-application", "branch", null));
	}

	@Test
	void createBranch_tooLongBranchName() {
		String branchName = RandomStringUtils.randomAlphabetic(256);
		ApplicationsApi api = buildClassUnderTest("{}");
		api.createBranch("my-application", branchName, new ApplicationProjectsParameter().addProject("my-project"));

		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertEquals(255, StringUtils.length(getFirstParameterValue(parameters, "branch")));
	}

	@Test
	void createBranch_noBranch() {
		ApplicationsApi api = buildClassUnderTest("{}");
		assertTrue(api.createBranch("my-application", "",
				new ApplicationProjectsParameter().addProject("my-project").addProjectBranch("projectBranch"))
				.isSuccess());
	}

	@Test
	void createBranch_noProjectBranch() {
		ApplicationsApi api = buildClassUnderTest("{}");
		assertTrue(api.createBranch("my-application", "branch",
				new ApplicationProjectsParameter().addProject("my-project"))
				.isSuccess());
	}

	@Test
	void deleteApplication() {
		ApplicationsApi api = buildClassUnderTest("{}");
		assertTrue(api.deleteApplication("my-application").isSuccess());
	}

	@Test
	void deleteApplication_noApplication() {
		ApplicationsApi api = buildClassUnderTest("{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.deleteApplication(""));
	}

	@Test
	void deleteApplication_serverError() {
		ApplicationsApi api = buildClassUnderTest(404, "{}");
		assertFalse(api.deleteApplication("my-application").isSuccess());
	}

	@Test
	void createApplication() {
		ApplicationsApi api = buildClassUnderTest(getCreateApplicationResponse());

		ApplicationResponse response = api.createApplication("My app", "My Application", "MY_APP", ApplicationVisibility.PUBLIC);
		assertNotNull(response);
		Application app = response.getApplication();
		assertAll(() -> assertEquals("My app", app.getName()),
				() -> assertEquals("My Application", app.getDescription()),
				() -> assertEquals("MY_APP", app.getKey()),
				() -> assertEquals("public", app.getVisibility()),
				() -> assertNotNull(app.getProjects()),
				() -> assertTrue(app.getProjects().isEmpty()));
	}

	@Test
	void createApplication_noName() {
		ApplicationsApi api = buildClassUnderTest(getCreateApplicationResponse());
		assertThrows(SonarQubeUnexpectedException.class, () -> api.createApplication(null, null, null, null));
	}

	@Test
	void createApplication_onlyName() {
		ApplicationsApi api = buildClassUnderTest(getCreateApplicationResponse());

		ApplicationResponse response = api.createApplication("My app", null, null, null);
		assertNotNull(response);
		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertAll(() -> assertNull(getFirstParameterValue(parameters, "description")),
				() -> assertNull(getFirstParameterValue(parameters, "key")),
				() -> assertNull(getFirstParameterValue(parameters, "visibility")));
	}

}
