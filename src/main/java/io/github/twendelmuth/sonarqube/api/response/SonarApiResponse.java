package io.github.twendelmuth.sonarqube.api.response;

import java.util.ArrayList;
import java.util.List;

import io.github.twendelmuth.sonarqube.coverage.ExcludeFromJacocoGeneratedReport;

public class SonarApiResponse {
	private int statusCode = -1;

	private String returnedBody;

	private final List<Error> errors = new ArrayList<>();

	public SonarApiResponse() {

	}

	public SonarApiResponse(int statusCode, String returnedBody) {
		super();
		this.statusCode = statusCode;
		this.returnedBody = returnedBody;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getReturnedBody() {
		return returnedBody;
	}

	public void setReturnedBody(String returnedBody) {
		this.returnedBody = returnedBody;
	}

	public boolean isSuccess() {
		return getStatusCode() >= 200 && getStatusCode() < 300;
	}

	@Override
	@ExcludeFromJacocoGeneratedReport
	public String toString() {
		return "SonarApiResponse [statusCode=" + statusCode + ", returnedBody=" + returnedBody + "]";
	}

	public List<Error> getErrors() {
		return errors;
	}

}
