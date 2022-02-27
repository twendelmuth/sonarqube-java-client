package io.github.twendelmuth.sonarqube.api.projects;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import io.github.twendelmuth.sonarqube.api.util.parameter.AbstractParameter;

public class ProjectFilterParameter extends AbstractParameter {

	private ZonedDateTime analyzedBeforeDateTime;

	private LocalDate analyzedBeforeDate;

	private Boolean onProvisionedOnly;

	private Set<String> projects = new LinkedHashSet<>();

	private String query;

	private Set<ProjectQualifier> qualifiers = new LinkedHashSet<>();

	private Integer page;

	private Integer pageSize;

	public enum ProjectQualifier {
		/**
		 * Projects
		 */
		TRK,
		/**
		 * Uncertain what this refers to.
		 */
		VW,
		/**
		 * Applications
		 */
		APP;

	}

	public String toParameterString() {
		StringBuilder parameterBuilder = new StringBuilder();
		if (analyzedBeforeDateTime != null) {
			addParameter(parameterBuilder, "analyzedBefore", formatDateForParameter(analyzedBeforeDateTime));
		} else if (analyzedBeforeDate != null) {
			addParameter(parameterBuilder, "analyzedBefore", formatDateForParameter(analyzedBeforeDate));
		}

		if (onProvisionedOnly != null) {
			addParameter(parameterBuilder, "onProvisionedOnly", onProvisionedOnly.toString());
		}

		if (!projects.isEmpty()) {
			addParameter(parameterBuilder, "projects", setToString(projects));
		}

		if (StringUtils.isNotBlank(query)) {
			addParameter(parameterBuilder, "q", query);
		}

		if (!qualifiers.isEmpty()) {
			addParameter(parameterBuilder, "qualifiers", setToString(qualifiers));
		}

		if (page != null && page > 0) {
			addParameter(parameterBuilder, "p", page.toString());
		}

		if (pageSize != null) {
			if (pageSize < 1) {
				pageSize = 1;
			} else if (pageSize > 500) {
				pageSize = 500;
			}

			addParameter(parameterBuilder, "ps", pageSize.toString());

		}

		return parameterBuilder.toString();
	}

	public static ProjectFilterBuilder bulkDeleteProjectFilterBuilder() {
		return new ProjectFilterBuilder();
	}

	public static ProjectSearchFilterBuilder searchProjectFilterBuilder() {
		return new ProjectSearchFilterBuilder();
	}

	public static class ProjectFilterBuilder {
		protected ProjectFilterParameter projectFilter = new ProjectFilterParameter();

		/**
		 * Filter the projects for which last analysis of any branch is older than the given date (exclusive).
		 * Either a date (server timezone) 
		 * or 
		 * datetime can be provided
		 * @see #analyzedBefore(LocalDate)
		 */
		public ProjectFilterBuilder analyzedBefore(ZonedDateTime analyzedBefore) {
			projectFilter.analyzedBeforeDateTime = analyzedBefore;
			return this;
		}

		/**
		 * Filter the projects for which last analysis of any branch is older than the given date (exclusive).
		 * Either a date (server timezone) 
		 * or 
		 * datetime can be provided
		 * @see #analyzedBefore(ZonedDateTime)	 */
		public ProjectFilterBuilder analyzedBefore(LocalDate analyzedBefore) {
			projectFilter.analyzedBeforeDate = analyzedBefore;
			return this;
		}

		/**
		 * Filter the projects that are provisioned
		 */
		public ProjectFilterBuilder provisionedOnly(boolean onProvisionedOnly) {
			projectFilter.onProvisionedOnly = onProvisionedOnly;
			return this;
		}

		/**
		 * Filter based on projectKey
		 * Can be called multiple times
		 */
		public ProjectFilterBuilder addProjectKey(String projectKey) {
			projectFilter.projects.add(projectKey);
			return this;
		}

		/**
		 * Limit to:
		 * component names that contain the supplied string
		 * component keys that contain the supplied string
		 */
		public ProjectFilterBuilder query(String query) {
			projectFilter.query = query;
			return this;
		}

		/**
		 * Filter the results with the specified qualifiers
		 * Can be called multiple times
		 */
		public ProjectFilterBuilder addQualifier(ProjectQualifier qualifier) {
			projectFilter.qualifiers.add(qualifier);
			return this;
		}

		public ProjectFilterParameter build() {
			return projectFilter;
		}

	}

	public static class ProjectSearchFilterBuilder extends ProjectFilterBuilder {

		/**
		 * 1-based page number
		 */
		public ProjectSearchFilterBuilder page(int page) {
			projectFilter.page = page;
			return this;
		}

		/**
		 * Page size. Must be greater than 0 and less or equal than 500
		 */
		public ProjectSearchFilterBuilder pageSize(int pageSize) {
			projectFilter.pageSize = pageSize;
			return this;
		}

	}

}
