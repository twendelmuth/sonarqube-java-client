package com.twendelmuth.sonarqube.api.ce.response;

import java.util.ArrayList;
import java.util.List;

import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ComponentResponse extends SonarApiResponse {

	private List<Task> queue = new ArrayList<>();

	private Task current;

	public List<Task> getQueue() {
		return queue;
	}

	public void setQueue(List<Task> queue) {
		this.queue = queue;
	}

	public Task getCurrent() {
		return current;
	}

	public void setCurrent(Task current) {
		this.current = current;
	}

}
