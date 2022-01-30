package com.twendelmuth.sonarqube.api;

import java.util.List;

import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public interface SonarQubeServer {

	SonarApiResponse doPost(String apiEndPoint, List<NameValuePair> parameters) throws SonarQubeServerError;

	SonarApiResponse doGet(String apiEndPoint) throws SonarQubeServerError;

}
