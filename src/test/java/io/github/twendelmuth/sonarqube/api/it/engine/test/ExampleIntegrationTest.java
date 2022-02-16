package io.github.twendelmuth.sonarqube.api.it.engine.test;

import org.junit.jupiter.api.Tag;

import io.github.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import io.github.twendelmuth.sonarqube.api.it.engine.ITest;
import io.github.twendelmuth.sonarqube.api.it.engine.IntegrationTest;

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
