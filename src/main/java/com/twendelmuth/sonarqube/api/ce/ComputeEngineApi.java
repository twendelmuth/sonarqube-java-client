package com.twendelmuth.sonarqube.api.ce;

import com.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.ce.response.ActivityResponse;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;

public class ComputeEngineApi extends AbstractApiEndPoint {

	protected static final String ACTIVITY = "/api/ce/activity";

	public ComputeEngineApi(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super(server, jsonMapper, logger);
	}

	public ActivityResponse getActivities(ActivitiesParameter parameter) {
		if (parameter == null) {
			parameter = new ActivitiesParameter();
		}
		return doGetWithErrorHandling(ACTIVITY + parameter.toParameterString(), ActivityResponse.class);
	}

}
