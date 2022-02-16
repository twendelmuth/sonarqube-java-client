package io.github.twendelmuth.sonarqube.api;

import java.util.List;

import io.github.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

public interface SonarQubeServer {

	SonarApiResponse doPost(String apiEndPoint, List<NameValuePair> parameters) throws SonarQubeServerError;

	SonarApiResponse doGet(String apiEndPoint) throws SonarQubeServerError;

}
