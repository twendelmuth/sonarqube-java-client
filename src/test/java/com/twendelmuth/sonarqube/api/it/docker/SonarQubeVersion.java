package com.twendelmuth.sonarqube.api.it.docker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.twendelmuth.sonarqube.api.SonarQubeLicense;

public enum SonarQubeVersion {
	V8_2("8.2-community", SonarQubeLicense.COMMUNITY),
	V8_9_LATEST("8.9-community", SonarQubeLicense.COMMUNITY),
	V8_9_LTS_DEVELOPER("8.9-developer", SonarQubeLicense.DEVELOPER),
	V8_9_LTS_ENTERPRISE("8.9-enterprise", SonarQubeLicense.ENTERPRISE),
	V9_LATEST("9-community", SonarQubeLicense.COMMUNITY),
	V9_LATEST_DEVELOPER("9-developer", SonarQubeLicense.DEVELOPER),
	V9_LATEST_ENTERPRISE("9-enterprise", SonarQubeLicense.ENTERPRISE);

	private final String dockerTag;

	private final SonarQubeLicense license;

	private SonarQubeVersion(String dockerTag, SonarQubeLicense license) {
		this.dockerTag = dockerTag;
		this.license = license;
	}

	public String getDockerTag() {
		return dockerTag;
	}

	public SonarQubeLicense getLicense() {
		return license;
	}

	public static List<SonarQubeVersion> getSonarQubeVersionsWithLicense(SonarQubeLicense license) {
		return Arrays.asList(SonarQubeVersion.values()).stream()
				.filter(version -> version.getLicense().compareTo(license) >= 0)
				.collect(Collectors.toList());
	}

	public static String[] getSonarQubeVersionsWithLicenseAsNameArray(SonarQubeLicense license) {
		return getSonarQubeVersionsWithLicense(license).stream()
				.map(version -> version.name())
				.collect(Collectors.toList())
				.toArray(new String[0]);
	}

}
