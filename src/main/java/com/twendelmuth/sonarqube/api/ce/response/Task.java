package com.twendelmuth.sonarqube.api.ce.response;

public class Task {
	private String id;

	private String type;

	private String componentId;

	private String componentKey;

	private String componentQualifier;

	private String analysisId;

	private String status;

	private String scannerContext;

	private String submittedAt;

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

	public String getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(String submittedAt) {
		this.submittedAt = submittedAt;
	}
}
