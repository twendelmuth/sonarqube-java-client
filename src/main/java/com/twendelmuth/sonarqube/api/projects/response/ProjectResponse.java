package com.twendelmuth.sonarqube.api.projects.response;

import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ProjectResponse extends SonarApiResponse {
	private Project project;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

}
