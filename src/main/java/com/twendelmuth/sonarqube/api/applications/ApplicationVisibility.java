package com.twendelmuth.sonarqube.api.applications;

public enum ApplicationVisibility {
	PUBLIC("public"),
	PRIVATE("private");

	private final String apiName;

	private ApplicationVisibility(String apiName) {
		this.apiName = apiName;
	}

	public String getApiName() {
		return apiName;
	}

}
