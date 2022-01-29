package com.twendelmuth.sonarqube.api.ce;

import org.apache.commons.lang3.StringUtils;

import com.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.ce.response.ActivityResponse;
import com.twendelmuth.sonarqube.api.ce.response.ActivityStatusResponse;
import com.twendelmuth.sonarqube.api.ce.response.ComponentResponse;
import com.twendelmuth.sonarqube.api.ce.response.TaskResponse;
import com.twendelmuth.sonarqube.api.exception.SonarQubeUnexpectedException;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;

public class ComputeEngineApi extends AbstractApiEndPoint {

	protected static final String ACTIVITY = "/api/ce/activity";

	protected static final String TASK = "/api/ce/task";

	protected static final String COMPONENT = "/api/ce/component";

	protected static final String ACTIVITY_STATUS = "/api/ce/activity_status";

	public ComputeEngineApi(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super(server, jsonMapper, logger);
	}

	/**
	 * Search for tasks.
	 * Either componentId or component can be provided, but not both.
	 * Requires the system administration permission, or project administration permission if componentId or component is set.
	 * 
	 */
	public ActivityResponse getActivities(ActivitiesParameter parameter) {
		if (parameter == null) {
			parameter = new ActivitiesParameter();
		}
		return doGetWithErrorHandling(ACTIVITY + parameter.toParameterString(), ActivityResponse.class);
	}

	/**
	 * Give Compute Engine task details such as type, status, duration and associated component.
	 * Requires 'Administer System' or 'Execute Analysis' permission.
	 */
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

	/**
	 * @see #getActivityStatus(String)
	 */
	public ActivityStatusResponse getActivityStatus() {
		return getActivityStatus(null);
	}

	/**
	 * Returns CE activity related metrics.
	 * Requires 'Administer System' permission or 'Administer' rights on the specified project.
	 */
	public ActivityStatusResponse getActivityStatus(String component) {
		StringBuilder parameterBuilder = new StringBuilder();
		if (StringUtils.isNotBlank(component)) {
			parameterBuilder.append("?component=").append(component);
		}

		return doGetWithErrorHandling(ACTIVITY_STATUS + parameterBuilder.toString(), ActivityStatusResponse.class);
	}

	/**
	 * Get the pending tasks, in-progress tasks and the last executed task of a given component (usually a project).
	 * Requires the following permission: 'Browse' on the specified component.
	 */
	public ComponentResponse getComponent(String component) {
		if (StringUtils.isBlank(component)) {
			throw new SonarQubeUnexpectedException("Component cannot be empty");
		}

		String parameter = "?component=" + component;
		return doGetWithErrorHandling(COMPONENT + parameter, ComponentResponse.class);
	}

}
