package com.twendelmuth.sonarqube.api.applications.model;

import java.util.ArrayList;
import java.util.List;

import com.twendelmuth.sonarqube.api.projects.response.Project;

public class Application {
	private String key;

	private String name;

	private String description;

	private String visibility;

	private List<Project> projects = new ArrayList<>();

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
}
