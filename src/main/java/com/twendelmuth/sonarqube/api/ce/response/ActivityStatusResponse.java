package com.twendelmuth.sonarqube.api.ce.response;

import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class ActivityStatusResponse extends SonarApiResponse {

	private int pending;

	private int inProgress;

	private int failing;

	private long pendingTime;

	public int getPending() {
		return pending;
	}

	public void setPending(int pending) {
		this.pending = pending;
	}

	public int getInProgress() {
		return inProgress;
	}

	public void setInProgress(int inProgress) {
		this.inProgress = inProgress;
	}

	public int getFailing() {
		return failing;
	}

	public void setFailing(int failing) {
		this.failing = failing;
	}

	public long getPendingTime() {
		return pendingTime;
	}

	public void setPendingTime(long pendingTime) {
		this.pendingTime = pendingTime;
	}

}
