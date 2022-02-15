package com.twendelmuth.sonarqube.api.it.engine.test;

import org.junit.jupiter.api.Tag;

import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.it.engine.ITest;
import com.twendelmuth.sonarqube.api.it.engine.IntegrationTest;

@IntegrationTest(shouldStartServer = false)
@Tag("IntegrationTest")
public class ExampleIntegrationTest {

	@ITest
	void test(SonarQubeVersion version) {

	}

	@ITest
	void test2(SonarQubeVersion version) {

	}
}
