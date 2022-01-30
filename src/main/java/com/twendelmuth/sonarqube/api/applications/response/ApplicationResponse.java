package com.twendelmuth.sonarqube.api.applications.response;

import com.twendelmuth.sonarqube.api.applications.model.Application;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ApplicationResponse extends SonarApiResponse {
	private Application application;

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}
