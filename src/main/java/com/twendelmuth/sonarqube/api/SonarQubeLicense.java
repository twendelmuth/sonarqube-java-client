package com.twendelmuth.sonarqube.api;

import org.apache.commons.lang3.StringUtils;

public enum SonarQubeLicense {
	COMMUNITY,
	DEVELOPER,
	ENTERPRISE;

	public static SonarQubeLicense getSonarQubeLicense(String name) {
		if (StringUtils.isBlank(name)) {
			return COMMUNITY;
		}

		try {
			return SonarQubeLicense.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			return COMMUNITY;
		}
	}

}
