package com.twendelmuth.sonarqube.api.it.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;

public class IntegrationSettings {
	private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationSettings.class);

	public static final String FILE_LOCATION = "sonarQube_integration_config";

	/**
	 * Which SonarQube versions are available for the Integration tests?
	 * Comma separated {@link SonarQubeVersion#name()} list
	 */
	public static final String VERSIONS = "sonarqube.versions";

	private Properties configProperties;

	public IntegrationSettings() {
		super();

	}

	private Properties getProperties() {
		if (configProperties == null) {
			configProperties = initProperties();
		}
		return configProperties;
	}

	@VisibleForTesting
	public Properties initProperties() {
		configProperties = new Properties(getSystemProperties());
		try {
			if (getConfigFileInputStream() != null) {
				configProperties.load(getConfigFileInputStream());
			}
		} catch (Exception e) {
			LOGGER.info("Couldn't load integrationTest properties, assuming defaults");
		}
		return configProperties;
	}

	protected Properties getSystemProperties() {
		return System.getProperties();
	}

	protected InputStream getConfigFileInputStream() throws FileNotFoundException {
		String systemFileLocation = getSystemProperties().getProperty(FILE_LOCATION);
		if (StringUtils.isNotBlank(systemFileLocation) && new File(systemFileLocation).exists()) {
			return new FileInputStream(new File(systemFileLocation));
		}

		return getClass().getClassLoader().getResourceAsStream(systemFileLocation);
	}

	public Set<SonarQubeVersion> getAvailableSonarQubeVersions() {
		Set<SonarQubeVersion> sonarQubeVersions = new HashSet<>();

		Properties props = getProperties();
		String versionString = props.getProperty(VERSIONS);
		if (StringUtils.isNotEmpty(versionString)) {
			Arrays.asList(StringUtils.split(versionString, ",")).forEach(version -> {
				try {
					sonarQubeVersions.add(SonarQubeVersion.valueOf(version));
				} catch (Exception e) {
					LOGGER.warn("Didn't understand, couldn't parse: " + version);
				}
			});
		}

		if (sonarQubeVersions.isEmpty()) {
			sonarQubeVersions.addAll(Arrays.asList(SonarQubeVersion.values()));
		}
		return sonarQubeVersions;
	}

}
