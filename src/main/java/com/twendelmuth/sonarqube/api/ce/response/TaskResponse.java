package com.twendelmuth.sonarqube.api.ce.response;

import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class TaskResponse extends SonarApiResponse {

	private Task task;

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
