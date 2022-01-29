package com.twendelmuth.sonarqube.api.it.docker;

public enum SonarQubeVersion {
	V8_9_LATEST("8.9-community"),
	V9_LATEST("9-community");

	private final String dockerTag;

	private SonarQubeVersion(String dockerTag) {
		this.dockerTag = dockerTag;
	}

	public String getDockerTag() {
		return dockerTag;
	}

}
