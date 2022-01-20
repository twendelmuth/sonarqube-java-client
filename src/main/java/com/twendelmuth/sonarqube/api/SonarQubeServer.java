package com.twendelmuth.sonarqube.api;

import java.util.Map;

import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public interface SonarQubeServer {

	SonarApiResponse doPost(String apiEndPoint, Map<String, String> parameters) throws SonarQubeServerError;

	SonarApiResponse doGet(String apiEndPoint) throws SonarQubeServerError;

}
