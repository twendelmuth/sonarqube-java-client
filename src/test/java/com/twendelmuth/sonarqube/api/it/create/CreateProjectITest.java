package com.twendelmuth.sonarqube.api.it.create;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.twendelmuth.sonarqube.api.SonarQubeClient;
import com.twendelmuth.sonarqube.api.components.ComponentsApi;
import com.twendelmuth.sonarqube.api.components.response.SearchProjectResponse;
import com.twendelmuth.sonarqube.api.it.SonarQubeClientIntegrationHelper;
import com.twendelmuth.sonarqube.api.projects.ProjectsApi;

class CreateProjectITest {

	private SonarQubeClient client;

	@BeforeEach
	void setup() {
		client = new SonarQubeClientIntegrationHelper().getSonarQubeClient();
	}

	/**
	 * Integration test, executing:
	 * 
	 * {@link ProjectsApi#create(String, String)} 
	 * {@link ComponentsApi#searchProjects(String, int, int)}
	 */
	@Test
	void createProject() {
		assertTrue(client.projectsApi().create("my-project", "my-project"));
		SearchProjectResponse searchResponse = client.componentsApi().searchProjects("my-project", 1, 1);
		Assertions.assertAll(
				() -> assertNotNull(searchResponse),
				() -> assertEquals(200, searchResponse.getStatusCode()),
				() -> assertEquals("OriginalBody: " + searchResponse.getReturnedBody(), 1, searchResponse.getPaging().getTotal()));
	}

}
