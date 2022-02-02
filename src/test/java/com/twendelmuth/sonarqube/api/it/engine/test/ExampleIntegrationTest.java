package com.twendelmuth.sonarqube.api.it.engine.test;

import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.it.engine.ITest;
import com.twendelmuth.sonarqube.api.it.engine.IntegrationTest;

@IntegrationTest(shouldStartServer = false)
public class ExampleIntegrationTest {

	@ITest
	void test(SonarQubeVersion version) {

	}

	@ITest
	void test2(SonarQubeVersion version) {

	}
}
