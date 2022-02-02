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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;
import com.twendelmuth.sonarqube.api.it.engine.IntegrationSettings;

class IntegrationSettingsTest {

	@Test
	void testDefaults() {
		IntegrationSettings integrationSettings = new IntegrationSettings();
		assertEquals(SonarQubeVersion.values().length, integrationSettings.getAvailableSonarQubeVersions().size());
	}

	@Test
	void testCustomFilePath() {
		IntegrationSettings integrationSettings = new IntegrationSettings() {
			@Override
			protected InputStream getConfigFileInputStream() throws FileNotFoundException {
				return getClass().getResourceAsStream("integration_test.properties");
			}
		};
		assertEquals(1, integrationSettings.getAvailableSonarQubeVersions().size());
	}

	@Test
	void testConfigPathIsCorrect() {
		IntegrationSettings integrationSettings = new IntegrationSettings() {
			@Override
			protected InputStream getConfigFileInputStream() throws FileNotFoundException {
				return getClass().getClassLoader().getResourceAsStream("config/integration_demo.properties");
			}
		};
		assertEquals(1, integrationSettings.getAvailableSonarQubeVersions().size());
	}

	@Test
	void testConfigPathInSystemProperty() throws Exception {
		Path tempPath = Files.createTempFile("tmp_integration_" + RandomStringUtils.randomAlphabetic(16), ".properties");
		File tempFile = tempPath.toFile();
		Properties systemProps = new Properties();
		systemProps.put(IntegrationSettings.FILE_LOCATION, tempFile.getAbsolutePath());

		try {
			FileOutputStream fos = new FileOutputStream(tempFile);
			IOUtils.write(IntegrationSettings.VERSIONS + "=" + SonarQubeVersion.V9_LATEST_DEVELOPER.name(), fos, StandardCharsets.UTF_8);
			IntegrationSettings integrationSettings = new IntegrationSettings() {
				@Override
				protected Properties getSystemProperties() {
					return systemProps;
				}
			};
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

		IntegrationSettings integrationSettings = new IntegrationSettings() {
			@Override
			protected Properties getSystemProperties() {
				return systemProps;
			}
		};
		assertEquals(1, integrationSettings.getAvailableSonarQubeVersions().size());
		assertTrue(integrationSettings.getAvailableSonarQubeVersions().contains(SonarQubeVersion.V9_LATEST_DEVELOPER));
	}

	@Test
	void useSystemAsDefault_commaSeparated() {
		Properties systemProps = new Properties();
		systemProps.put(IntegrationSettings.VERSIONS, SonarQubeVersion.V9_LATEST_DEVELOPER.name() + "," + SonarQubeVersion.V9_LATEST_ENTERPRISE);

		IntegrationSettings integrationSettings = new IntegrationSettings() {
			@Override
			protected Properties getSystemProperties() {
				return systemProps;
			}
		};
		assertEquals(2, integrationSettings.getAvailableSonarQubeVersions().size());
		assertTrue(integrationSettings.getAvailableSonarQubeVersions().contains(SonarQubeVersion.V9_LATEST_DEVELOPER));
		assertTrue(integrationSettings.getAvailableSonarQubeVersions().contains(SonarQubeVersion.V9_LATEST_ENTERPRISE));
	}

}
