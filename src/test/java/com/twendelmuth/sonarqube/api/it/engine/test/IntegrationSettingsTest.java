package com.twendelmuth.sonarqube.api.it.engine.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.it.engine.IntegrationSettings;

class IntegrationSettingsTest {

	private IntegrationSettings buildIntegrationSettings(Properties properties) {
		return new IntegrationSettings() {
			@Override
			protected Properties getSystemProperties() {
				return properties;
			}
		};
	}

	private IntegrationSettings buildIntegrationSettings(InputStream configInputStream) {
		return new IntegrationSettings() {
			@Override
			protected InputStream getConfigFileInputStream() throws FileNotFoundException {
				return configInputStream;
			}
		};
	}

	@Test
	void testDefaults() {
		IntegrationSettings integrationSettings = buildIntegrationSettings(new Properties());
		assertEquals(SonarQubeVersion.values().length, integrationSettings.getAvailableSonarQubeVersions().size());
	}

	@Test
	void testCustomFilePath() {
		IntegrationSettings integrationSettings = buildIntegrationSettings(getClass().getResourceAsStream("integration_test.properties"));
		assertEquals(1, integrationSettings.getAvailableSonarQubeVersions().size());
	}

	@Test
	void testConfigPathIsCorrect() {
		IntegrationSettings integrationSettings = buildIntegrationSettings(
				getClass().getClassLoader().getResourceAsStream("config/integration_demo.properties"));
		assertEquals(1, integrationSettings.getAvailableSonarQubeVersions().size());
	}

	@Test
	void testConfigPathInSystemProperty() throws Exception {
		Path tempPath = Files.createTempFile("tmp_integration_" + RandomStringUtils.randomAlphabetic(16), ".properties");
		File tempFile = tempPath.toFile();
		Properties systemProps = new Properties();
		systemProps.put(IntegrationSettings.FILE_LOCATION, tempFile.getAbsolutePath());

		try {
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				IOUtils.write(IntegrationSettings.VERSIONS + "=" + SonarQubeVersion.V9_LATEST_DEVELOPER.name(), fos, StandardCharsets.UTF_8);
			}
			IntegrationSettings integrationSettings = buildIntegrationSettings(systemProps);
			assertEquals(1, integrationSettings.getAvailableSonarQubeVersions().size());
			assertTrue(integrationSettings.getAvailableSonarQubeVersions().contains(SonarQubeVersion.V9_LATEST_DEVELOPER));
		} finally {
			tempFile.delete();
		}
	}

	@Test
	void onlyTryToLoadResourcesOnce() {
		IntegrationSettings integrationSettings = Mockito.spy(IntegrationSettings.class);
		integrationSettings.getAvailableSonarQubeVersions();
		integrationSettings.getAvailableSonarQubeVersions();
		Mockito.verify(integrationSettings, Mockito.times(1)).initProperties();
	}

	@Test
	void useSystemAsDefault() {
		Properties systemProps = new Properties();
		systemProps.put(IntegrationSettings.VERSIONS, SonarQubeVersion.V9_LATEST_DEVELOPER.name());

		IntegrationSettings integrationSettings = buildIntegrationSettings(systemProps);
		assertEquals(1, integrationSettings.getAvailableSonarQubeVersions().size());
		assertTrue(integrationSettings.getAvailableSonarQubeVersions().contains(SonarQubeVersion.V9_LATEST_DEVELOPER));
	}

	@Test
	void useSystemAsDefault_commaSeparated() {
		Properties systemProps = new Properties();
		systemProps.put(IntegrationSettings.VERSIONS, SonarQubeVersion.V9_LATEST_DEVELOPER.name() + "," + SonarQubeVersion.V9_LATEST_ENTERPRISE);

		IntegrationSettings integrationSettings = buildIntegrationSettings(systemProps);
		assertEquals(2, integrationSettings.getAvailableSonarQubeVersions().size());
		assertTrue(integrationSettings.getAvailableSonarQubeVersions().contains(SonarQubeVersion.V9_LATEST_DEVELOPER));
		assertTrue(integrationSettings.getAvailableSonarQubeVersions().contains(SonarQubeVersion.V9_LATEST_ENTERPRISE));
	}

	@Test
	void homeDir_fileDoesntExist() {
		String tempDir = System.getProperty("java.io.tmpdir");
		Properties systemProps = new Properties();
		systemProps.put(IntegrationSettings.SYSTEM_HOME_DIR, tempDir);

		IntegrationSettings integrationSettings = buildIntegrationSettings(systemProps);
		assertEquals(SonarQubeVersion.values().length, integrationSettings.getAvailableSonarQubeVersions().size());
	}

	@Test
	void homeDir_fileConfiguration() throws Exception {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		Properties systemProps = new Properties();
		systemProps.put(IntegrationSettings.SYSTEM_HOME_DIR, tempDir.getAbsolutePath());

		IntegrationSettings integrationSettings = buildIntegrationSettings(systemProps);

		File tempFile = new File(tempDir + IntegrationSettings.HOME_DIR_FILE);
		try {
			if (tempFile.exists()) {
				tempFile.delete();
			}
			FileUtils.createParentDirectories(tempFile);
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				IOUtils.write(IntegrationSettings.VERSIONS + "=" + SonarQubeVersion.V9_LATEST_DEVELOPER.name(), fos, StandardCharsets.UTF_8);
			}
			assertEquals(1, integrationSettings.getAvailableSonarQubeVersions().size());
			assertTrue(integrationSettings.getAvailableSonarQubeVersions().contains(SonarQubeVersion.V9_LATEST_DEVELOPER));
		} finally {
			FileUtils.delete(tempFile);
			String parentDir = tempFile.getParent();
			while (!StringUtils.equalsAnyIgnoreCase(parentDir, tempDir.getAbsolutePath())) {

				File parentDirectory = new File(parentDir);
				FileUtils.delete(parentDirectory);
				parentDir = parentDirectory.getParent();
			}
		}
	}

}
