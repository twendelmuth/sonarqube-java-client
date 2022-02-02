package com.twendelmuth.sonarqube.api.projects;

import com.twendelmuth.sonarqube.coverage.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
public enum ProjectVisibility {
	PUBLIC("public"),
	PRIVATE("private");

	private final String apiName;

	private ProjectVisibility(String apiName) {
		this.apiName = apiName;
	}

	public String getApiName() {
		return apiName;
	}

}
