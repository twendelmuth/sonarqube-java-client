package io.github.twendelmuth.sonarqube.api.it.engine;

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

import io.github.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;

public class IntegrationSettings {
	private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationSettings.class);

	public static final String SYSTEM_HOME_DIR = "user.home";

	public static final String FILE_LOCATION = "sonarQube_integration_config";

	public static final String HOME_DIR_FILE = "/.twendelmuth/.sonarqube/integrationTests.properties";

	public static final String LOOKUP_HOME_DIR = "sonarqube_integration_lookup_homeDir";

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
		try (InputStream configInputStream = getConfigFileInputStream()) {
			if (configInputStream != null) {
				configProperties.load(configInputStream);
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
		File homeDirFile = getFileFromHomeDir();
		if (StringUtils.isNotBlank(systemFileLocation) && new File(systemFileLocation).exists()) {
			return new FileInputStream(new File(systemFileLocation));
		} else if (homeDirFile != null) {
			return new FileInputStream(homeDirFile);
		}

		return getClass().getClassLoader().getResourceAsStream(systemFileLocation);
	}

	protected File getFileFromHomeDir() {
		String lookupHomeDir = getSystemProperties().getProperty(LOOKUP_HOME_DIR);
		if (hasHomeDir() && (StringUtils.isBlank(lookupHomeDir) || StringUtils.equalsIgnoreCase("true", lookupHomeDir))) {
			File file = new File(getSystemProperties().getProperty(SYSTEM_HOME_DIR) + HOME_DIR_FILE);
			if (file.exists()) {
				return file;
			}
		}

		return null;
	}

	private boolean hasHomeDir() {
		return StringUtils.isNotBlank(getSystemProperties().getProperty(SYSTEM_HOME_DIR));
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
