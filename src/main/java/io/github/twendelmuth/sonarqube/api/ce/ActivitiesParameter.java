package io.github.twendelmuth.sonarqube.api.ce;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class ActivitiesParameter {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssZ");

	private String component;

	private ZonedDateTime maxExecutedAt;

	private ZonedDateTime minSubmittedAt;

	private Boolean onlyCurrents;

	private Integer pageSize;

	private String query;

	private Set<ActivitiesStatus> status = new LinkedHashSet<>();

	private ActivitiesType type;

	public String toParameterString() {
		StringBuilder parameterBuilder = new StringBuilder();

		if (component != null) {
			addParameter(parameterBuilder, "component", component);
		}

		if (maxExecutedAt != null) {
			addParameter(parameterBuilder, "maxExecutedAt", formatDateForParameter(maxExecutedAt));
		}

		if (minSubmittedAt != null) {
			addParameter(parameterBuilder, "minSubmittedAt", formatDateForParameter(minSubmittedAt));
		}

		if (onlyCurrents != null) {
			addParameter(parameterBuilder, "onlyCurrents", onlyCurrents.toString());
		}

		if (pageSize != null) {
			if (pageSize < 1) {
				pageSize = 1;
			} else if (pageSize > 1000) {
				pageSize = 1000;
			}

			addParameter(parameterBuilder, "ps", pageSize.toString());
		}

		if (StringUtils.isNotBlank(query)) {
			addParameter(parameterBuilder, "q", query);
		}

		if (!status.isEmpty()) {
			addParameter(parameterBuilder, "status", getStatusString(status));
		}

		if (type != null) {
			addParameter(parameterBuilder, "type", type.name());
		}

		return parameterBuilder.toString();
	}

	private String formatDateForParameter(ZonedDateTime date) {
		return DATE_FORMATTER.format(date).replace("+", "%2B");
	}

	private String getStatusString(Set<ActivitiesStatus> statusSet) {
		StringBuilder statusString = new StringBuilder();
		statusSet.forEach(innerStatus -> {
			if (statusString.length() > 0) {
				statusString.append(",");
			}
			statusString.append(innerStatus.name());
		});

		return statusString.toString();
	}

	private StringBuilder addParameter(StringBuilder builder, String key, String value) {
		if (builder.length() == 0) {
			builder.append("?");
		} else {
			builder.append("&");
		}

		builder.append(key).append("=").append(value);
		return builder;
	}

	public enum ActivitiesStatus {
		SUCCESS,
		FAILED,
		CANCELED,
		PENDING,
		IN_PROGRESS
	}

	public enum ActivitiesType {
		REPORT,
		ISSUE_SYNC,
		AUDIT_PURGE,
		PROJECT_EXPORT,
		APP_REFRESH
	}

	public static ActivitiesParameterBuilder builder() {
		return new ActivitiesParameter().new ActivitiesParameterBuilder();
	}

	public class ActivitiesParameterBuilder {
		private ActivitiesParameter build = new ActivitiesParameter();

		/**
		 * 
		 * @param component
		 * @return
		 */
		public ActivitiesParameterBuilderComponentAlreadySet component(String component) {
			build.component = component;
			return new ActivitiesParameterBuilderComponentAlreadySet(this);
		}

		public ActivitiesParameterBuilder maxExecutedAt(ZonedDateTime maxExecutedAt) {
			build.maxExecutedAt = maxExecutedAt;
			return this;
		}

		public ActivitiesParameterBuilder minSubmittedAt(ZonedDateTime minSubmittedAt) {
			build.minSubmittedAt = minSubmittedAt;
			return this;
		}

		public ActivitiesParameterBuilder onlyCurrents(boolean onlyCurrents) {
			build.onlyCurrents = onlyCurrents;
			return this;
		}

		public ActivitiesParameterBuilder pageSize(int pageSize) {
			build.pageSize = pageSize;
			return this;
		}

		public ActivitiesParameterBuilderQueryAlreadySet query(String query) {
			build.query = query;
			return new ActivitiesParameterBuilderQueryAlreadySet(this);
		}

		public ActivitiesParameterBuilder addStatus(ActivitiesStatus status) {
			build.status.add(status);
			return this;
		}

		public ActivitiesParameterBuilder type(ActivitiesType type) {
			build.type = type;
			return this;
		}

		public ActivitiesParameter build() {
			return build;
		}
	}

	public class ActivitiesParameterBuilderQueryAlreadySet {
		private ActivitiesParameter build;

		public ActivitiesParameterBuilderQueryAlreadySet(ActivitiesParameterBuilder parameterBuilder) {
			this.build = parameterBuilder.build;
		}

		public ActivitiesParameterBuilderQueryAlreadySet maxExecutedAt(ZonedDateTime maxExecutedAt) {
			build.maxExecutedAt = maxExecutedAt;
			return this;
		}

		public ActivitiesParameterBuilderQueryAlreadySet minSubmittedAt(ZonedDateTime minSubmittedAt) {
			build.minSubmittedAt = minSubmittedAt;
			return this;
		}

		public ActivitiesParameterBuilderQueryAlreadySet onlyCurrents(boolean onlyCurrents) {
			build.onlyCurrents = onlyCurrents;
			return this;
		}

		public ActivitiesParameterBuilderQueryAlreadySet pageSize(int pageSize) {
			build.pageSize = pageSize;
			return this;
		}

		public ActivitiesParameterBuilderQueryAlreadySet query(String query) {
			build.query = query;
			return this;
		}

		public ActivitiesParameterBuilderQueryAlreadySet addStatus(ActivitiesStatus status) {
			build.status.add(status);
			return this;
		}

		public ActivitiesParameterBuilderQueryAlreadySet type(ActivitiesType type) {
			build.type = type;
			return this;
		}

		public ActivitiesParameter build() {
			return build;
		}
	}

	public class ActivitiesParameterBuilderComponentAlreadySet {
		private ActivitiesParameter build;

		public ActivitiesParameterBuilderComponentAlreadySet(ActivitiesParameterBuilder parameterBuilder) {
			this.build = parameterBuilder.build;
		}

		public ActivitiesParameterBuilderComponentAlreadySet component(String component) {
			build.component = component;
			return this;
		}

		public ActivitiesParameterBuilderComponentAlreadySet maxExecutedAt(ZonedDateTime maxExecutedAt) {
			build.maxExecutedAt = maxExecutedAt;
			return this;
		}

		public ActivitiesParameterBuilderComponentAlreadySet minSubmittedAt(ZonedDateTime minSubmittedAt) {
			build.minSubmittedAt = minSubmittedAt;
			return this;
		}

		public ActivitiesParameterBuilderComponentAlreadySet onlyCurrents(boolean onlyCurrents) {
			build.onlyCurrents = onlyCurrents;
			return this;
		}

		public ActivitiesParameterBuilderComponentAlreadySet pageSize(int pageSize) {
			build.pageSize = pageSize;
			return this;
		}

		public ActivitiesParameterBuilderComponentAlreadySet addStatus(ActivitiesStatus status) {
			build.status.add(status);
			return this;
		}

		public ActivitiesParameterBuilderComponentAlreadySet type(ActivitiesType type) {
			build.type = type;
			return this;
		}

		public ActivitiesParameter build() {
			return build;
		}
	}

}
