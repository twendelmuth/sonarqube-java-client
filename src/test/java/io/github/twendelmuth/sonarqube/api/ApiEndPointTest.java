package io.github.twendelmuth.sonarqube.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.twendelmuth.sonarqube.api.exception.SonarQubeUnexpectedException;
import io.github.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;

class ApiEndPointTest {
	private SonarQubeTestLogger testLogger;

	private SonarQubeJsonMapper jsonMapper;

	private SonarQubeServer sonarQubeServer;

	@BeforeEach
	void setup() {
		testLogger = Mockito.spy(SonarQubeTestLogger.class);
		jsonMapper = Mockito.spy(SonarQubeJacksonMapper.class);
		sonarQubeServer = Mockito.mock(SonarQubeServer.class);
	}

	private AbstractApiEndPoint buildApiEndPoint(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new AbstractApiEndPoint(sonarQubeServer, jsonMapper, testLogger) {

		};
	}

	@Test
	void serverNull() {
		Assertions.assertThrows(SonarQubeUnexpectedException.class, () -> {
			buildApiEndPoint(null, jsonMapper, testLogger);
		});
	}

	@Test
	void jsonMapperNull() {
		Assertions.assertThrows(SonarQubeUnexpectedException.class, () -> {
			buildApiEndPoint(sonarQubeServer, null, testLogger);
		});
	}

	@Test
	void loggerNull() {
		Assertions.assertThrows(SonarQubeUnexpectedException.class, () -> {
			buildApiEndPoint(sonarQubeServer, jsonMapper, null);
		});
	}

}
