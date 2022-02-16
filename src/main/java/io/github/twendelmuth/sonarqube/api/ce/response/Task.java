package io.github.twendelmuth.sonarqube.api.ce.response;

import java.time.ZonedDateTime;

public class Task {
	private String organization;

	private String id;

	private String type;

	private String componentId;

	private String componentKey;

	private String componentName;

	private String componentQualifier;

	private String analysisId;

	private String status;

	private String submitterLogin;

	private Long executionTimeMs;

	private boolean hasErrorStacktrace;

	private boolean hasScannerContext;

	private ZonedDateTime submittedAt;

	private ZonedDateTime startedAt;

	private ZonedDateTime executedAt;

	public ZonedDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(ZonedDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

	public ZonedDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(ZonedDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public ZonedDateTime getExecutedAt() {
		return executedAt;
	}

	public void setExecutedAt(ZonedDateTime executedAt) {
		this.executedAt = executedAt;
	}

	private String scannerContext;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentKey() {
		return componentKey;
	}

	public void setComponentKey(String componentKey) {
		this.componentKey = componentKey;
	}

	public String getComponentQualifier() {
		return componentQualifier;
	}

	public void setComponentQualifier(String componentQualifier) {
		this.componentQualifier = componentQualifier;
	}

	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getScannerContext() {
		return scannerContext;
	}

	public void setScannerContext(String scannerContext) {
		this.scannerContext = scannerContext;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getSubmitterLogin() {
		return submitterLogin;
	}

	public void setSubmitterLogin(String submitterLogin) {
		this.submitterLogin = submitterLogin;
	}

	public Long getExecutionTimeMs() {
		return executionTimeMs;
	}

	public void setExecutionTimeMs(Long executionTimeMs) {
		this.executionTimeMs = executionTimeMs;
	}

	public boolean isHasErrorStacktrace() {
		return hasErrorStacktrace;
	}

	public void setHasErrorStacktrace(boolean hasErrorStacktrace) {
		this.hasErrorStacktrace = hasErrorStacktrace;
	}

	public boolean isHasScannerContext() {
		return hasScannerContext;
	}

	public void setHasScannerContext(boolean hasScannerContext) {
		this.hasScannerContext = hasScannerContext;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

}
