package io.github.twendelmuth.sonarqube.api.response;

import io.github.twendelmuth.sonarqube.coverage.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
public class Error {
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
