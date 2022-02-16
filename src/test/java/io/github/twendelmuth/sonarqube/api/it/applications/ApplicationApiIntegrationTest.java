package io.github.twendelmuth.sonarqube.api.it.applications;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;

import io.github.twendelmuth.sonarqube.api.SonarQubeClient;
import io.github.twendelmuth.sonarqube.api.SonarQubeLicense;
import io.github.twendelmuth.sonarqube.api.applications.ApplicationProjectsParameter;
import io.github.twendelmuth.sonarqube.api.applications.ApplicationVisibility;
import io.github.twendelmuth.sonarqube.api.applications.ApplicationsApi;
import io.github.twendelmuth.sonarqube.api.applications.ApplicationsApiTest;
import io.github.twendelmuth.sonarqube.api.applications.model.Application;
import io.github.twendelmuth.sonarqube.api.applications.response.ApplicationResponse;
import io.github.twendelmuth.sonarqube.api.it.AbstractSonarQubeIntegrationTest;
import io.github.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import io.github.twendelmuth.sonarqube.api.it.engine.ITest;
import io.github.twendelmuth.sonarqube.api.it.engine.IntegrationTest;
import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

@Tag("IntegrationTest")
@IntegrationTest(license = SonarQubeLicense.DEVELOPER)
class ApplicationApiIntegrationTest extends AbstractSonarQubeIntegrationTest {

	private static final String MY_APP = "MY_APP";

	private ApplicationResponse createApplication(SonarQubeClient client) {
		ApplicationResponse response = client.applicationsApi().createApplication("My app", "My Application", MY_APP, ApplicationVisibility.PUBLIC);
		assertNotNull(response);
		assertTrue(response.isSuccess());
		cleanUpList.add(() -> client.applicationsApi().deleteApplication(MY_APP));
		return response;
	}

	@ITest
	void createApplicationTest(SonarQubeVersion version) throws Exception {
		SonarQubeClient client = createClient(version);
		ApplicationResponse response = createApplication(client);

		Application returnedApplication = response.getApplication();
		assertAll("Couldn't assert application, returnBody: " + response.getReturnedBody() + ", status: " + response.getStatusCode(),
				() -> assertTrue(response.isSuccess()),
				() -> assertEquals("My app", returnedApplication.getName()),
				() -> assertEquals("My Application", returnedApplication.getDescription()),
				() -> assertEquals(MY_APP, returnedApplication.getKey()));
		assertTrue(response.getErrors().isEmpty());

		String returnBody = response.getReturnedBody();
		assertJsonIsTheSame(ApplicationsApiTest.getCreateApplicationResponse(), returnBody);
	}

	//	@ITest
	// can't be tested? Found no way to create a branch in a project via API.
	void createBranch_withBranch(SonarQubeVersion version) {
		SonarQubeClient client = createClient(version);
		ApplicationsApi applicationsApi = client.applicationsApi();

		createApplication(client);
		createProject(client, "my-project");

		SonarApiResponse response = applicationsApi.createBranch(MY_APP, "my-branch",
				new ApplicationProjectsParameter().addProject("my-project").addProjectBranch("project-branch"));
		assertTrue(response.isSuccess(), buildResponseInformation("Call wasn't successful!", response));
		assertTrue(response.getErrors().isEmpty());
	}

	@ITest
	void showApplication(SonarQubeVersion version) throws Exception {
		SonarQubeClient client = createClient(version);
		createApplication(client);

		ApplicationResponse response = client.applicationsApi().getApplication(MY_APP);

		Application returnedApplication = response.getApplication();
		assertAll("Couldn't assert application, returnBody: " + response.getReturnedBody() + ", status: " + response.getStatusCode(),
				() -> assertTrue(response.isSuccess()),
				() -> assertEquals("My app", returnedApplication.getName()),
				() -> assertEquals("My Application", returnedApplication.getDescription()),
				() -> assertEquals(MY_APP, returnedApplication.getKey()));
		assertTrue(response.getErrors().isEmpty());
	}

}
