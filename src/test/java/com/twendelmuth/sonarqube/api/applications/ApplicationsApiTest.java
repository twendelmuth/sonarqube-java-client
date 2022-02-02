package com.twendelmuth.sonarqube.api.applications;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
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
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

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

	@Test
	void deleteBranch() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.deleteBranch("MY_APP", "myBranch");
		assertTrue(response.isSuccess());
		assertEquals(0, response.getErrors().size());
	}

	@Test
	void deleteBranch_appParamMissing() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.deleteBranch("", "myBranch"));
	}

	@Test
	void deleteBranch_branchParamMissing() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.deleteBranch("MY_APP", ""));
	}

	@Test
	void deleteBranch_notFound() {
		ApplicationsApi api = buildClassUnderTest(404, "{}");
		SonarApiResponse response = api.deleteBranch("MY_APP", "myBranch");
		assertFalse(response.isSuccess());
	}

	@Test
	void removeProject() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.removeProject("MY_APP", "my-project");
		assertTrue(response.isSuccess());
	}

	@Test
	void removeProject_noApplication() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.removeProject("", "my-project"));
	}

	@Test
	void removeProject_noProject() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.removeProject("MY_APP", ""));
	}

	@Test
	void removeProject_notFound() {
		ApplicationsApi api = buildClassUnderTest(404, "{}");
		SonarApiResponse response = api.removeProject("MY_APP", "my-project");
		assertFalse(response.isSuccess());
	}

	@Test
	void setTags() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.setTags("MY_APP", "tag1,tag2");
		assertTrue(response.isSuccess());
	}

	@Test
	void setTags_nullToEmpty() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.setTags("MY_APP", (String) null);
		assertTrue(response.isSuccess());

		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertNotNull(getFirstParameterValue(parameters, ApplicationsApi.TAGS_PARAMETER));
	}

	@Test
	void setTags_noApplication() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.setTags("", "tag1"));
	}

	@Test
	void setTags_useCollection() {
		List<String> tags = new ArrayList<>();
		tags.add("tag1");
		tags.add("tag2");
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.setTags("MY_APP", tags);
		assertTrue(response.isSuccess());

		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertEquals("tag1,tag2", getFirstParameterValue(parameters, ApplicationsApi.TAGS_PARAMETER));
	}

	@Test
	void setTags_useEmptyCollection() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.setTags("MY_APP", Collections.EMPTY_LIST);
		assertTrue(response.isSuccess());

		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertNotNull(getFirstParameterValue(parameters, ApplicationsApi.TAGS_PARAMETER));
	}

	@Test
	void updateApplication() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.updateApplication("MY_APP", "name", "description");
		assertTrue(response.isSuccess());
		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertEquals("MY_APP", getFirstParameterValue(parameters, ApplicationsApi.APPLICATION_PARAMETER));
		assertEquals("name", getFirstParameterValue(parameters, ApplicationsApi.NAME_PARAMETER));
		assertEquals("description", getFirstParameterValue(parameters, ApplicationsApi.DESCRIPTION_PARAMETER));
	}

	@Test
	void updateApplication_noApplication() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.updateApplication("", "", "description"));
	}

	@Test
	void updateApplication_noName() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		assertThrows(SonarQubeUnexpectedException.class, () -> api.updateApplication("MY_APP", "", "description"));
	}

	@Test
	void updateApplication_noDescription() {
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.updateApplication("MY_APP", "name", "");
		assertTrue(response.isSuccess());
		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertNull(getFirstParameterValue(parameters, ApplicationsApi.DESCRIPTION_PARAMETER));
	}

	@Test
	void updateApplication_nameOver255Chars() {
		String name = RandomStringUtils.randomAlphabetic(256);
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.updateApplication("MY_APP", name, "");
		assertTrue(response.isSuccess());
		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertEquals(255, getFirstParameterValue(parameters, ApplicationsApi.NAME_PARAMETER).length());
	}

	@Test
	void updateApplication_descriptionOver255Chars() {
		String description = RandomStringUtils.randomAlphabetic(256);
		ApplicationsApi api = buildClassUnderTest(200, "{}");
		SonarApiResponse response = api.updateApplication("MY_APP", "name", description);
		assertTrue(response.isSuccess());
		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertEquals(255, getFirstParameterValue(parameters, ApplicationsApi.DESCRIPTION_PARAMETER).length());
	}
}
