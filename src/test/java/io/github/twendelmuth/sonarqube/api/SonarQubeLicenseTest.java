package io.github.twendelmuth.sonarqube.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SonarQubeLicenseTest {

	@Test
	void testEmpty() {
		assertEquals(SonarQubeLicense.COMMUNITY, SonarQubeLicense.getSonarQubeLicense(""));
	}

	@Test
	void testUnknown() {
		assertEquals(SonarQubeLicense.COMMUNITY, SonarQubeLicense.getSonarQubeLicense("unknown"));
	}

	@Test
	void testCommunity() {
		assertEquals(SonarQubeLicense.COMMUNITY, SonarQubeLicense.getSonarQubeLicense("community"));
	}

	@Test
	void testDeveloper() {
		assertEquals(SonarQubeLicense.DEVELOPER, SonarQubeLicense.getSonarQubeLicense("developer"));
	}

	@Test
	void testEnterprise() {
		assertEquals(SonarQubeLicense.ENTERPRISE, SonarQubeLicense.getSonarQubeLicense("enterprise"));
	}
}
