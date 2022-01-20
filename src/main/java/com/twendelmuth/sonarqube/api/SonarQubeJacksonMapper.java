package com.twendelmuth.sonarqube.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;

public class SonarQubeJacksonMapper implements SonarQubeJsonMapper {

	@Override
	public <T> T transformStringToObject(String json, Class<T> targetClass) throws SonarQubeClientJsonException {
		try {
			return getObjectMapper().readValue(json, targetClass);
		} catch (JsonProcessingException e) {
			throw new SonarQubeClientJsonException("Error during json parsing", e);
		}
	}

	protected ObjectMapper getObjectMapper() {
		return JsonMapper.builder()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.build();
	}

}
