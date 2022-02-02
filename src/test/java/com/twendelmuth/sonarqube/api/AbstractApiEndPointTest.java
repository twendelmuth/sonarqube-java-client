package com.twendelmuth.sonarqube.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;
import com.twendelmuth.sonarqube.testing.util.UrlTools;

public abstract class AbstractApiEndPointTest<T extends AbstractApiEndPoint> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApiEndPointTest.class);

	private SonarQubeTestLogger testLogger;

	private SonarQubeJsonMapper jsonMapper;

	private SonarQubeServer sonarQubeServer;

	@BeforeEach
	void setup() {
		testLogger = Mockito.spy(SonarQubeTestLogger.class);
		jsonMapper = Mockito.spy(SonarQubeJacksonMapper.class);
		sonarQubeServer = Mockito.mock(SonarQubeServer.class);
	}

	protected T buildClassUnderTest(int status, String jsonResult) {
		try {
			doReturn(new SonarApiResponse(status, jsonResult)).when(sonarQubeServer).doGet(any());
			doReturn(new SonarApiResponse(status, jsonResult)).when(sonarQubeServer).doPost(any(), any());
		} catch (SonarQubeServerError e) {
		}

		return buildTestUnderTest(sonarQubeServer, jsonMapper, testLogger);
	}

	protected T buildClassUnderTest(String jsonResult) {
		return buildClassUnderTest(200, jsonResult);
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
		return IOHelper.getStringFromResource(this.getClass(), resource);
	}

	protected Map<String, String> getParameterMapFromGetRequest() {
		try {
			ArgumentCaptor<String> endpointParameter = ArgumentCaptor.forClass(String.class);
			Mockito.verify(getSonarQubeServer()).doGet(endpointParameter.capture());
			return UrlTools.extractQueryParameterMap(endpointParameter.getValue());
		} catch (Exception e) {
			LOGGER.warn("Exception while trying to get parameterMap from GET-request", e);
			return new HashMap<>();
		}
	}

	protected String getEndpointFromPostRequest() {
		try {
			ArgumentCaptor<String> endpoint = ArgumentCaptor.forClass(String.class);
			Mockito.verify(getSonarQubeServer()).doPost(endpoint.capture(), any());
			return endpoint.getValue();
		} catch (Exception e) {
			LOGGER.warn("Exception while trying to get endpoint from POST-request", e);
			return "";
		}
	}

	protected List<NameValuePair> getParameterListFromPostRequest() {
		try {
			ArgumentCaptor<List<NameValuePair>> endpointParameter = ArgumentCaptor.forClass(List.class);
			Mockito.verify(getSonarQubeServer()).doPost(anyString(), endpointParameter.capture());
			return endpointParameter.getValue();
		} catch (Exception e) {
			LOGGER.warn("Exception while trying to get parameterMap from POST-request", e);
			return new ArrayList<>();
		}
	}

	protected String getFirstParameterValue(List<NameValuePair> nameValuePairList, String key) {
		NameValuePair nameValuePair = nameValuePairList.stream().filter(nvp -> nvp.getName().equals(key)).findFirst().orElse(null);
		if (nameValuePair == null) {
			return null;
		}

		return nameValuePair.getValue();
	}

	protected List<String> getAllParameterValues(List<NameValuePair> nameValuePairList, String key) {
		return nameValuePairList.stream()
				.filter(nvp -> nvp.getName().equals(key))
				.map(nvp -> nvp.getValue())
				.collect(Collectors.toList());
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
