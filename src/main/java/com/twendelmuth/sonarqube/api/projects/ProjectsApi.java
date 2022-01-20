package com.twendelmuth.sonarqube.api.projects;

import java.util.HashMap;
import java.util.Map;

import com.twendelmuth.sonarqube.api.AbstractApiEndPoint;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.logging.SonarQubeLogger;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ProjectsApi extends AbstractApiEndPoint {
	private static final String CREATE = "/api/projects/create";

	public ProjectsApi(SonarQubeServer server, SonarQubeJsonMapper jsonMapper, SonarQubeLogger logger) {
		super(server, jsonMapper, logger);
	}

	public boolean create(String name, String project) {
		Map<String, String> parameters = new HashMap<>();

		parameters.put("name", name);
		parameters.put("project", project);

		SonarApiResponse response = doPostWithErrorHandling(CREATE, parameters, SonarApiResponse.class);
		return response.getStatusCode() == 200;
	}

}
