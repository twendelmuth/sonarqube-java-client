package io.github.twendelmuth.sonarqube.api.projects.response;

public class Project {
	private String key;

	private String name;

	private String qualifier;

	private String visibility;

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

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

}
