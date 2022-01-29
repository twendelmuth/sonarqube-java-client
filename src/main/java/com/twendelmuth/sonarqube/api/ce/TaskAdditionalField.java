package com.twendelmuth.sonarqube.api.ce;

public enum TaskAdditionalField {
	STACKTRACE("stacktrace"),
	SCANNERCONTEXT("scannerContext"),
	WARNINGS("warnings");

	private final String name;

	private TaskAdditionalField(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
