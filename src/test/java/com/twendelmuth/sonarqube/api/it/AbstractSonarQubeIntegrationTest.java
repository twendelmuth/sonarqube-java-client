package com.twendelmuth.sonarqube.api.it;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twendelmuth.sonarqube.api.SonarQubeClient;
import com.twendelmuth.sonarqube.api.it.docker.SonarQubeDockerContainer;
import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

@Tag("IntegrationTest")
public abstract class AbstractSonarQubeIntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSonarQubeIntegrationTest.class);

	protected static final String POTENTIAL_WRONG_UNIT_TEST_ASSUMPTIONS = "Returned body didn't match our expectations, Unit Tests might be working with wrong assumptions";

	protected List<Supplier<Object>> cleanUpList = new ArrayList<>();

	@AfterEach
	protected void cleanup() {
		cleanUpList.forEach(supplier -> supplier.get());
	}

	//	@BeforeAll
	protected static void startAllSonarQubeServers() {
		Arrays.asList(SonarQubeVersion.values()).stream()
				.forEach(version -> {
					StopWatch bootClock = StopWatch.createStarted();
					SonarQubeDockerContainer.build(version).startSonarQubeContainer();
					bootClock.stop();
					LOGGER.info("Booted up SonarQube Server {} in {} ms", version.name(), bootClock.getTime(TimeUnit.MILLISECONDS));
				});
	}

	//	@AfterAll
	protected static void shutDownAllSonarQubeServers() {
		Arrays.asList(SonarQubeVersion.values()).stream()
				.forEach(version -> {
					SonarQubeDockerContainer.build(version).stopSonarQubeContainer();
				});
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
		SonarApiResponse response = client.projectsApi().create(key, key);
		cleanUpList.add(() -> deleteProject(client, key));
		return response;
	}

	protected SonarApiResponse deleteProject(SonarQubeClient client, String key) {
		return client.projectsApi().delete(key);
	}

}
