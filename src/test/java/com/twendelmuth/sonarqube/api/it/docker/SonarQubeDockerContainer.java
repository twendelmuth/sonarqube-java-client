package com.twendelmuth.sonarqube.api.it.docker;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.twendelmuth.sonarqube.api.SonarQubeJacksonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServerHttpClient;
import com.twendelmuth.sonarqube.api.exception.SonarQubeClientException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;

public class SonarQubeDockerContainer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SonarQubeDockerContainer.class);

	private static Map<SonarQubeVersion, SonarQubeDockerContainer> CONTAINER_MAP = new HashMap<>();

	private final SonarQubeVersion sonarQubeVersion;

	private GenericContainer<?> sonarQubeServerContainer;

	private String apiToken;

	private SonarQubeDockerContainer(SonarQubeVersion sonarQubeVersion) {
		super();
		this.sonarQubeVersion = sonarQubeVersion;
	}

	public static SonarQubeDockerContainer build(SonarQubeVersion version) {
		return CONTAINER_MAP.computeIfAbsent(version, (v) -> new SonarQubeDockerContainer(v));
	}

	public void startSonarQubeContainer() {
		if (sonarQubeServerContainer == null) {
			sonarQubeServerContainer = new GenericContainer<>("sonarqube:" + sonarQubeVersion.getDockerTag())
					.withExposedPorts(9000)
					.waitingFor(Wait.forLogMessage(".*WebServer is operational.*", 1)
							.withStartupTimeout(Duration.ofMinutes(2)));
			sonarQubeServerContainer.start();
			apiToken = generateServerToken(sonarQubeServerContainer);
		}
	}

	public void stopSonarQubeContainer() {
		if (sonarQubeServerContainer != null) {
			sonarQubeServerContainer.stop();
		} else {
			LOGGER.warn("Tried to stop server that wasn't running, version: {}", sonarQubeVersion.name());
		}
	}

	public String getApiToken() {
		if (sonarQubeServerContainer == null) {
			startSonarQubeContainer();
		}

		return apiToken;
	}

	private String generateServerToken(GenericContainer<?> sonarQubeServerContainer) {
		SonarQubeServerHttpClient httpClient = new SonarQubeServerHttpClient(getServerConnectionString(), null) {
			@Override
			protected void authorizationHeaders(HttpRequest httpRequest) {
				httpRequest.addHeader("Authorization", basicAuth("admin", "admin"));
			}
		};

		Map<String, String> parameters = new HashMap<>();
		parameters.put("login", "admin");
		parameters.put("name", "it-token");

		try {
			String response = httpClient.doPost("/api/user_tokens/generate", parameters).getReturnedBody();
			return new SonarQubeJacksonMapper().transformStringToObject(response, ApiTokenResponse.class).getToken();
		} catch (SonarQubeServerError | SonarQubeClientJsonException e) {
			throw new SonarQubeClientException("Couldn't create apiToken", e);
		}

	}

	public String getServerConnectionString() {
		return "http://" + sonarQubeServerContainer.getHost() + ":" + sonarQubeServerContainer.getMappedPort(9000);
	}

}
