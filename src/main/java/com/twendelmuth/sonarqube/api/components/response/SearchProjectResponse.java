package com.twendelmuth.sonarqube.api.components.response;

import java.util.ArrayList;
import java.util.List;

import com.twendelmuth.sonarqube.api.response.Component;
import com.twendelmuth.sonarqube.api.response.Paging;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class SearchProjectResponse extends SonarApiResponse {

	private Paging paging = new Paging();

	private List<Component> components = new ArrayList<>();

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}
}
