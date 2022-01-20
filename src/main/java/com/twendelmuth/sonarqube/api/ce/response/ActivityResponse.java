package com.twendelmuth.sonarqube.api.ce.response;

import java.util.List;

import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ActivityResponse extends SonarApiResponse {
	private List<Task> tasks;

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
}
