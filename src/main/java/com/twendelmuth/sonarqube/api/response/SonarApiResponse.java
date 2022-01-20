package com.twendelmuth.sonarqube.api.response;

public class SonarApiResponse {
	private int statusCode = -1;

	private String returnedBody;

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

	@Override
	public String toString() {
		return "SonarApiResponse [statusCode=" + statusCode + ", returnedBody=" + returnedBody + "]";
	}

}
