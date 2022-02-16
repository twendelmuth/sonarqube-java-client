package io.github.twendelmuth.sonarqube.api;

import io.github.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;

public interface SonarQubeJsonMapper {

	<T> T transformStringToObject(String json, Class<T> targetClass) throws SonarQubeClientJsonException;
}
