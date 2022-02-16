package io.github.twendelmuth.sonarqube.api.applications;

import java.util.ArrayList;
import java.util.List;

public class ApplicationProjectsParameter {

	private final List<String> projectsList = new ArrayList<>();

	private final List<String> projectBranchesList = new ArrayList<>();

	public List<String> getProjectsList() {
		return projectsList;
	}

	public List<String> getProjectBranchesList() {
		return projectBranchesList;
	}

	public ApplicationProjectsParameter addProject(String project) {
		projectsList.add(project);
		return this;
	}

	public ApplicationProjectsParameter addProjectBranch(String projectBranch) {
		projectBranchesList.add(projectBranch);
		return this;
	}

	public boolean areProjectsEmpty() {
		return projectsList.isEmpty();
	}

	public boolean areProjectBranchesEmpty() {
		return projectBranchesList.isEmpty();
	}

}
