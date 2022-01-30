package com.twendelmuth.sonarqube.api.it.applications;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twendelmuth.sonarqube.api.SonarQubeClient;
import com.twendelmuth.sonarqube.api.SonarQubeLicense;
import com.twendelmuth.sonarqube.api.SonarQubeVersionEnum;
import com.twendelmuth.sonarqube.api.applications.ApplicationProjectsParameter;
import com.twendelmuth.sonarqube.api.applications.ApplicationVisibility;
import com.twendelmuth.sonarqube.api.applications.ApplicationsApi;
import com.twendelmuth.sonarqube.api.applications.ApplicationsApiTest;
import com.twendelmuth.sonarqube.api.applications.model.Application;
import com.twendelmuth.sonarqube.api.applications.response.ApplicationResponse;
import com.twendelmuth.sonarqube.api.it.AbstractSonarQubeIntegrationTest;
import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

@Tag("IntegrationTest")
class ApplicationApiIntegrationTest extends AbstractSonarQubeIntegrationTest {

	private static final String MY_APP = "MY_APP";

	private ApplicationResponse createApplication(SonarQubeClient client) {
		ApplicationResponse response = client.applicationsApi().createApplication("My app", "My Application", MY_APP, ApplicationVisibility.PUBLIC);
		assertNotNull(response);
		assertTrue(response.isSuccess());
		cleanUpList.add(() -> client.applicationsApi().deleteApplication(MY_APP));
		return response;
	}

	@ParameterizedTest
	@SonarQubeVersionEnum(license = SonarQubeLicense.DEVELOPER)
	void createApplicationTest(SonarQubeVersion version) throws Exception {
		SonarQubeClient client = createClient(version);
		ApplicationResponse response = createApplication(client);

		Application returnedApplication = response.getApplication();
		assertAll("Couldn't assert application, returnBody: " + response.getReturnedBody() + ", status: " + response.getStatusCode(),
				() -> assertEquals("My app", returnedApplication.getName()),
				() -> assertEquals("My Application", returnedApplication.getDescription()),
				() -> assertEquals(MY_APP, returnedApplication.getKey()));
		assertTrue(response.getErrors().isEmpty());

		String returnBody = response.getReturnedBody();
		//make sure our normal test assumptions are correct.
		ObjectMapper objectMapper = new ObjectMapper();
		assertEquals(objectMapper.readTree(ApplicationsApiTest.getCreateApplicationResponse()), objectMapper.readTree(returnBody),
				POTENTIAL_WRONG_UNIT_TEST_ASSUMPTIONS);
	}

	@ParameterizedTest
	@SonarQubeVersionEnum(license = SonarQubeLicense.DEVELOPER)
	void createBranch_noBranch(SonarQubeVersion version) {
		SonarQubeClient client = createClient(version);
		ApplicationsApi applicationsApi = client.applicationsApi();

		createApplication(client);
		createProject(client, "my-project");

		SonarApiResponse response = applicationsApi.createBranch(MY_APP, "",
				new ApplicationProjectsParameter().addProject("my-project"));
		assertTrue(response.isSuccess(), buildResponseInformation("Call wasn't successful!", response));
		assertTrue(response.getErrors().isEmpty());
	}

	@ParameterizedTest
	@SonarQubeVersionEnum(license = SonarQubeLicense.DEVELOPER)
	void createBranch_withBranch(SonarQubeVersion version) {
		SonarQubeClient client = createClient(version);
		ApplicationsApi applicationsApi = client.applicationsApi();

		createApplication(client);
		createProject(client, "my-project");

		SonarApiResponse response = applicationsApi.createBranch(MY_APP, "my-branch",
				new ApplicationProjectsParameter().addProject("my-project"));
		assertTrue(response.isSuccess(), buildResponseInformation("Call wasn't successful!", response));
		assertTrue(response.getErrors().isEmpty());
	}

}
