package com.twendelmuth.sonarqube.api.it.engine.test;

import java.util.function.Consumer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.EventStatistics;

import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.it.engine.IntegrationSettings;

class IntegrationEngineTest {

	private static String disableIntegrationFile;

	@BeforeAll
	static void beforeAll() {
		disableIntegrationFile = System.getProperty(IntegrationSettings.LOOKUP_HOME_DIR);
		System.setProperty(IntegrationSettings.LOOKUP_HOME_DIR, "false");
	}

	@AfterAll
	static void afterAll() {
		if (disableIntegrationFile != null) {
			System.setProperty(IntegrationSettings.LOOKUP_HOME_DIR, disableIntegrationFile);
		} else {
			System.clearProperty(IntegrationSettings.LOOKUP_HOME_DIR);
		}
	}

	private Consumer<EventStatistics> assertStatistics(int tests) {
		int sqVersions = new IntegrationSettings().getAvailableSonarQubeVersions().size();

		return stats -> stats
				.started(sqVersions * tests)
				.succeeded(sqVersions * tests)
				.failed(0);
	}

	@Test
	void testIntegrationEngine_selectClass() {
		EngineTestKit
				.engine("SonarQubeIntegrationTestEngine")
				.selectors(DiscoverySelectors.selectClass(ExampleIntegrationTest.class))
				.execute()
				.testEvents()
				.assertStatistics(assertStatistics(2));
	}

	@Test
	void testIntegrationEngine_selectPackage() {
		EngineTestKit
				.engine("SonarQubeIntegrationTestEngine")
				.selectors(DiscoverySelectors.selectPackage("com.twendelmuth.sonarqube.api.it.engine.test"))
				.execute()
				.testEvents()
				.assertStatistics(assertStatistics(2));
	}

	@Test
	void testIntegrationEngine_selectMethod() {
		EngineTestKit
				.engine("SonarQubeIntegrationTestEngine")
				.selectors(DiscoverySelectors.selectMethod(ExampleIntegrationTest.class, "test", SonarQubeVersion.class.getCanonicalName()))
				.execute()
				.testEvents()
				.assertStatistics(assertStatistics(1));
	}

	@Test
	void dontRunIntegrationTests() {
		EngineTestKit
				.engine("SonarQubeIntegrationTestEngine")
				.selectors(DiscoverySelectors.selectMethod(ExampleIntegrationTest.class, "test", SonarQubeVersion.class.getCanonicalName()))
				.filters(TagFilter.excludeTags("IntegrationTest"))
				.execute()
				.testEvents()
				.assertStatistics(assertStatistics(0));
	}

}
