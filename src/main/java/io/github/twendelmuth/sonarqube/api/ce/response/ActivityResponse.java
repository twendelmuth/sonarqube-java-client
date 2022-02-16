package io.github.twendelmuth.sonarqube.api.ce.response;

import java.util.List;

import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ActivityResponse extends SonarApiResponse {
	private List<Task> tasks;

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
}
