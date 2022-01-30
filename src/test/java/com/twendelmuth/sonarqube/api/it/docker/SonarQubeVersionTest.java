package com.twendelmuth.sonarqube.api.it.docker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.twendelmuth.sonarqube.api.SonarQubeLicense;

class SonarQubeVersionTest {

	private int countEditions(SonarQubeLicense license) {
		int count = 0;
		for (SonarQubeVersion version : SonarQubeVersion.values()) {
			if (version.getLicense() == license) {
				count++;
			}
		}

		return count;
	}

	@Test
	void testCommunityEditions() {
		assertEquals(
				countEditions(SonarQubeLicense.COMMUNITY) + countEditions(SonarQubeLicense.DEVELOPER) + countEditions(SonarQubeLicense.ENTERPRISE),
				SonarQubeVersion.getSonarQubeVersionsWithLicense(SonarQubeLicense.COMMUNITY).size());
	}

	@Test
	void testDeveloperEditions() {
		assertEquals(
				countEditions(SonarQubeLicense.DEVELOPER) + countEditions(SonarQubeLicense.ENTERPRISE),
				SonarQubeVersion.getSonarQubeVersionsWithLicense(SonarQubeLicense.DEVELOPER).size());
	}

	@Test
	void testEnterpriseEditions() {
		assertEquals(
				countEditions(SonarQubeLicense.ENTERPRISE),
				SonarQubeVersion.getSonarQubeVersionsWithLicense(SonarQubeLicense.ENTERPRISE).size());
	}

}
