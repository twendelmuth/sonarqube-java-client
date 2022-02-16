package io.github.twendelmuth.sonarqube.api.projects;

import io.github.twendelmuth.sonarqube.coverage.ExcludeFromJacocoGeneratedReport;

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
