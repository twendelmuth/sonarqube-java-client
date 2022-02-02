package com.twendelmuth.sonarqube.api.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twendelmuth.sonarqube.api.SonarQubeClient;
import com.twendelmuth.sonarqube.api.exception.SonarQubeUnexpectedException;
import com.twendelmuth.sonarqube.api.it.docker.SonarQubeDockerContainer;
import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

@Tag("IntegrationTest")
public abstract class AbstractSonarQubeIntegrationTest {
	protected static final String POTENTIAL_WRONG_UNIT_TEST_ASSUMPTIONS = "Returned body didn't match our expectations, Unit Tests might be working with wrong assumptions";

	protected List<Supplier<Object>> cleanUpList = new ArrayList<>();

	@AfterEach
	protected void cleanup() {
		cleanUpList.forEach(supplier -> supplier.get());
	}

	protected void assertJsonIsTheSame(String expected, String actual) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			assertEquals(objectMapper.readTree(expected), objectMapper.readTree(actual),
					POTENTIAL_WRONG_UNIT_TEST_ASSUMPTIONS);
		} catch (JsonProcessingException e) {
			throw new SonarQubeUnexpectedException("Error during asseration", e);
		}
	}

	protected SonarQubeClient createClient(SonarQubeVersion version) {
		SonarQubeDockerContainer container = SonarQubeDockerContainer.build(version);
		container.startSonarQubeContainer();
		SonarQubeClient client = new SonarQubeClient(container.getServerConnectionString(), container.getApiToken());
		return client;
	}

	protected String buildResponseInformation(String message, SonarApiResponse response) {
		return message + "; status: " + response.getStatusCode() + "; body: " + response.getReturnedBody();
	}

	protected SonarApiResponse createProject(SonarQubeClient client, String key) {
		return createProject(client, key, key);
	}

	protected SonarApiResponse createProject(SonarQubeClient client, String key, String name) {
		SonarApiResponse response = client.projectsApi().create(name, key);
		cleanUpList.add(() -> deleteProject(client, key));
		return response;
	}

	protected SonarApiResponse deleteProject(SonarQubeClient client, String key) {
		return client.projectsApi().delete(key);
	}

}
