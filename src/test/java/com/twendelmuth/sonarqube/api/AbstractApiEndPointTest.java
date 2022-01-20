package com.twendelmuth.sonarqube.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public abstract class AbstractApiEndPointTest<T extends AbstractApiEndPoint> {
	private SonarQubeTestLogger testLogger;

	private SonarQubeJsonMapper jsonMapper;

	private SonarQubeServer sonarQubeServer;

	@BeforeEach
	void setup() {
		testLogger = Mockito.spy(SonarQubeTestLogger.class);
		jsonMapper = Mockito.spy(SonarQubeJacksonMapper.class);
		sonarQubeServer = Mockito.mock(SonarQubeServer.class);
	}

	protected T buildClassUnderTest(String jsonResult) {
		try {
			doReturn(new SonarApiResponse(200, jsonResult)).when(sonarQubeServer).doGet(any());
			doReturn(new SonarApiResponse(200, jsonResult)).when(sonarQubeServer).doPost(any(), any());
		} catch (SonarQubeServerError e) {
		}

		return buildTestUnderTest(sonarQubeServer, jsonMapper, testLogger);
	}

	protected T buildClassUnderTest(String jsonResult, SonarQubeClientJsonException jsonException) {
		try {
			doReturn(new SonarApiResponse(200, jsonResult)).when(sonarQubeServer).doGet(any());
			doReturn(new SonarApiResponse(200, jsonResult)).when(sonarQubeServer).doPost(any(), any());
			doThrow(jsonException).when(jsonMapper).transformStringToObject(any(), any());
		} catch (SonarQubeServerError | SonarQubeClientJsonException e) {
		}

		return buildTestUnderTest(sonarQubeServer, jsonMapper, testLogger);
	}

	protected T buildClassUnderTest(SonarQubeServerError serverError) {
		try {
			doThrow(serverError).when(sonarQubeServer).doGet(any());
			doThrow(serverError).when(sonarQubeServer).doPost(any(), any());
		} catch (SonarQubeServerError e) {
		}
		return buildTestUnderTest(sonarQubeServer, jsonMapper, testLogger);
	}

	protected String getStringFromResource(String resource) {
		try {
			return IOUtils.toString(
					this.getClass().getResourceAsStream(resource),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read resource during test", e);
		}
	}

	public SonarQubeTestLogger getTestLogger() {
		return testLogger;
	}

	public SonarQubeJsonMapper getJsonMapper() {
		return jsonMapper;
	}

	public SonarQubeServer getSonarQubeServer() {
		return sonarQubeServer;
	}

	protected abstract T buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger);

}
