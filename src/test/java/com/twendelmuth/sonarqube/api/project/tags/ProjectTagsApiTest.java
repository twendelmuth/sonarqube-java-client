package com.twendelmuth.sonarqube.api.project.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import com.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import com.twendelmuth.sonarqube.api.SonarQubeServer;
import com.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

class ProjectTagsApiTest extends AbstractApiEndPointTest<ProjectTagsApi> {

	private final String projectKey = "my-project";

	@Override
	protected ProjectTagsApi buildTestUnderTest(SonarQubeServer sonarQubeServer, SonarQubeJsonMapper jsonMapper, SonarQubeTestLogger testLogger) {
		return new ProjectTagsApi(sonarQubeServer, jsonMapper, testLogger);
	}

	@Test
	void setTags_withSet() throws Exception {
		ProjectTagsApi api = buildClassUnderTest("{}");
		List<String> tagSet = new ArrayList<>();
		tagSet.add("tag-1");
		tagSet.add("tag-2");

		SonarApiResponse response = api.setTags(projectKey, tagSet);
		assertEquals(200, response.getStatusCode());

		ArgumentCaptor<Map<String, String>> parameterCaptor = ArgumentCaptor.forClass(HashMap.class);
		Mockito.verify(getSonarQubeServer()).doPost(any(), parameterCaptor.capture());
		assertEquals("tag-1,tag-2", parameterCaptor.getValue().get("tags"));
	}

	@Test
	void setTags_withString() throws Exception {
		ProjectTagsApi api = buildClassUnderTest("{}");
		String tags = "tag-1,tag-2";
		SonarApiResponse response = api.setTags(projectKey, tags);
		assertEquals(200, response.getStatusCode());

		ArgumentCaptor<Map<String, String>> parameterCaptor = ArgumentCaptor.forClass(HashMap.class);
		Mockito.verify(getSonarQubeServer()).doPost(any(), parameterCaptor.capture());
		assertEquals(tags, parameterCaptor.getValue().get("tags"));
	}

}
