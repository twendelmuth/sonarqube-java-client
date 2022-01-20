package com.twendelmuth.sonarqube.api.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.components.response.SearchProjectResponse;
import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;

public class ComponentsApiTest extends AbstractApiEndPointTest<ComponentsApi> {

	@Override
	protected ComponentsApi buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new ComponentsApi(sonarQubeServer, jsonMapper, testLogger);
	}

	@Test
	void testSearchProjects() {
		ComponentsApi componentsApi = buildClassUnderTest(getStringFromResource("search_projects.json"));

		SearchProjectResponse response = componentsApi.searchProjects("something", 100, 1);
		assertNotNull(response);
		assertEquals(3, response.getPaging().getTotal());

		assertEquals(3, response.getComponents().size());
		assertEquals("my_project", response.getComponents().get(0).getKey());
		assertEquals(2, response.getComponents().get(0).getTags().size());
	}

	@Test
	void testServerException() {
		ComponentsApi componentsApi = buildClassUnderTest(new SonarQubeServerError("Error", 403, "some body"));

		SearchProjectResponse response = componentsApi.searchProjects("something", 100, 1);
		assertNotNull(response);
		assertEquals(403, response.getStatusCode());
	}

	@Test
	void testSearchProjects_jsonIssues() {
		ComponentsApi componentsApi = buildClassUnderTest(getStringFromResource("search_projects.json"),
				new SonarQubeClientJsonException("Something happened", null));

		SearchProjectResponse response = componentsApi.searchProjects("something", 100, 1);
		assertNotNull(response);
		assertEquals(200, response.getStatusCode());
		assertEquals(1, getTestLogger().countErrorMessages(), "Expected to have one log warning!");
	}

	@Test
	void testSearchProjects_urlEncoding() throws Exception {
		ComponentsApi componentsApi = buildClassUnderTest(getStringFromResource("search_projects.json"));

		ArgumentCaptor<String> endpointParameter = ArgumentCaptor.forClass(String.class);
		SearchProjectResponse response = componentsApi.searchProjects("something", 100, 1);

		Mockito.verify(getSonarQubeServer()).doGet(endpointParameter.capture());
		String filterParameter = endpointParameter.getValue().split("filter=")[1];
		assertEquals("query%20%3D%20%22something%22", filterParameter);
	}

}
