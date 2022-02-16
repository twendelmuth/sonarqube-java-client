package io.github.twendelmuth.sonarqube.api.it.docker;

import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ApiTokenResponse extends SonarApiResponse {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
