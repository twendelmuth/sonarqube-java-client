package io.github.twendelmuth.sonarqube.api.exception;

public class SonarQubeServerError extends Exception {
	private static final long serialVersionUID = 1L;

	private final int statusCode;

	private final String body;

	public SonarQubeServerError(String message, int statusCode, String body) {
		super(message);
		this.statusCode = statusCode;
		this.body = body;
	}

	@Override
	public String toString() {
		return getMessage() + ", status: " + statusCode + ", response: " + body;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getBody() {
		return body;
	}
}
