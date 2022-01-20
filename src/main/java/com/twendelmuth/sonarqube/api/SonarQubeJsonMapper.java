package com.twendelmuth.sonarqube.api;

import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;

public interface SonarQubeJsonMapper {

	<T> T transformStringToObject(String json, Class<T> targetClass) throws SonarQubeClientJsonException;
}
