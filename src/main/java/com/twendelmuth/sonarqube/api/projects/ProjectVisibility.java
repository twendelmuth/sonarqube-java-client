package com.twendelmuth.sonarqube.api.projects;

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
