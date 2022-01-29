package com.twendelmuth.sonarqube.api.ce;

import com.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.ce.response.ActivityResponse;
import com.twendelmuth.sonarqube.api.ce.response.TaskResponse;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;

public class ComputeEngineApi extends AbstractApiEndPoint {

	protected static final String ACTIVITY = "/api/ce/activity";

	protected static final String TASK = "/api/ce/task";

	public ComputeEngineApi(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super(server, jsonMapper, logger);
	}

	public ActivityResponse getActivities(ActivitiesParameter parameter) {
		if (parameter == null) {
			parameter = new ActivitiesParameter();
		}
		return doGetWithErrorHandling(ACTIVITY + parameter.toParameterString(), ActivityResponse.class);
	}

	public TaskResponse getTask(String id, TaskAdditionalField... additionalFields) {
		StringBuilder parameters = new StringBuilder().append("?id=").append(id);

		StringBuilder additionalFieldsParameter = new StringBuilder();
		for (TaskAdditionalField additionalField : additionalFields) {
			if (additionalFieldsParameter.length() > 0) {
				additionalFieldsParameter.append(",");
			}
			additionalFieldsParameter.append(additionalField.getName());
		}

		if (additionalFieldsParameter.length() > 0) {
			parameters.append("&additionalFields=").append(additionalFieldsParameter.toString());
		}

		return doGetWithErrorHandling(TASK + parameters.toString(), TaskResponse.class);
	}

}
