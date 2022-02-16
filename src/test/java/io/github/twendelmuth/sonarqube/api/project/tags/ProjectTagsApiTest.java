package io.github.twendelmuth.sonarqube.api.project.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.twendelmuth.sonarqube.api.AbstractApiEndPointTest;
import io.github.twendelmuth.sonarqube.api.NameValuePair;
import io.github.twendelmuth.sonarqube.api.SonarQubeJsonMapper;
import io.github.twendelmuth.sonarqube.api.SonarQubeServer;
import io.github.twendelmuth.sonarqube.api.logging.SonarQubeTestLogger;
import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

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

		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertEquals("tag-1,tag-2", getFirstParameterValue(parameters, "tags"));
	}

	@Test
	void setTags_withString() throws Exception {
		ProjectTagsApi api = buildClassUnderTest("{}");
		String tags = "tag-1,tag-2";
		SonarApiResponse response = api.setTags(projectKey, tags);
		assertEquals(200, response.getStatusCode());

		List<NameValuePair> parameters = getParameterListFromPostRequest();
		assertEquals(tags, getFirstParameterValue(parameters, "tags"));
	}

}
