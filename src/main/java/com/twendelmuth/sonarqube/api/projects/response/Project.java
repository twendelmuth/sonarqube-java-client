package com.twendelmuth.sonarqube.api.projects.response;

import com.twendelmuth.sonarqube.api.projects.ProjectVisibility;

public class Project {
	private String key;

	private String name;

	private String qualifier;

	private ProjectVisibility visibility;

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

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public ProjectVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(ProjectVisibility visibility) {
		this.visibility = visibility;
	}
}
