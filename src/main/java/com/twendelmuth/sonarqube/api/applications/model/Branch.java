package com.twendelmuth.sonarqube.api.applications.model;

public class Branch {
	private String name;

	private boolean isMain;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMain() {
		return isMain;
	}

	public void setIsMain(boolean isMain) {
		this.isMain = isMain;
	}
}
