package io.github.twendelmuth.sonarqube.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.twendelmuth.sonarqube.api.exception.SonarQubeClientJsonException;

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
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.addModule(new JavaTimeModule())
				.enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
				.build();
	}

}
