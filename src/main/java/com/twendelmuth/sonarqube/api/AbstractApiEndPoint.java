package com.twendelmuth.sonarqube.api;

import java.util.Map;
import java.util.function.Function;

import com.twendelmuth.sonarqube.api.exception.SonarQubeClientException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;
import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class AbstractApiEndPoint {

	private final SonarQubeServer server;

	private final SonarQubeJsonMapper jsonMapper;

	private final SonarQubeLogger logger;

	public AbstractApiEndPoint(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super();
		this.server = server;
		this.jsonMapper = jsonMapper;
		this.logger = logger;
	}

	public SonarQubeLogger getLogger() {
		return logger;
	}

	public SonarQubeServer getServer() {
		return server;
	}

	protected <T extends SonarApiResponse> T doGetWithErrorHandling(String endpoint, Class<T> responseClass) {
		return handleHTTPConnection(httpCall -> {
			try {
				return getServer().doGet(endpoint);
			} catch (SonarQubeServerError e) {
				return new SonarApiResponse(e.getStatusCode(), e.getBody());
			}
		}, responseClass);
	}

	protected <T extends SonarApiResponse> T doPostWithErrorHandling(String endpoint, Map<String, String> parameters, Class<T> responseClass) {

		return handleHTTPConnection(httpCall -> {
			try {
				return getServer().doPost(endpoint, parameters);
			} catch (SonarQubeServerError e) {
				return new SonarApiResponse(e.getStatusCode(), e.getBody());
			}
		}, responseClass);
	}

	private <T extends SonarApiResponse> T handleHTTPConnection(Function<Void, SonarApiResponse> httpFunction, Class<T> responseClass) {
		T response = null;
		SonarApiResponse originalResponse = null;
		try {
			originalResponse = httpFunction.apply(null);
			response = jsonMapper.transformStringToObject(originalResponse.getReturnedBody(), responseClass);
		} catch (SonarQubeClientJsonException e) {
			getLogger().logError("Issues while parsing json response", e);
		}

		if (response == null) {
			try {
				response = responseClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new SonarQubeClientException("Couldn't create instance of: " + responseClass, e);
			}
		}

		response.setReturnedBody(originalResponse.getReturnedBody());
		response.setStatusCode(originalResponse.getStatusCode());

		return response;

	}

}
