package io.github.twendelmuth.sonarqube.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SonarQubeClientTest {

	private static final String LOGIN_TOKEN = "none";

	@Test
	void serverUrl_httpsServer() {
		assertEquals("https://localhost", new SonarQubeClient("https://localhost", LOGIN_TOKEN).getServerUrl());
	}

	@Test
	void serverUrl_httpServer() {
		assertEquals("http://localhost", new SonarQubeClient("http://localhost", LOGIN_TOKEN).getServerUrl());
	}

	@Test
	void serverUrl_endsInSlash() {
		assertEquals("http://localhost", new SonarQubeClient("http://localhost/", LOGIN_TOKEN).getServerUrl());
	}

	@Test
	void serverUrl_endsInMultipleSlashes() {
		assertEquals("http://localhost", new SonarQubeClient("http://localhost/////", LOGIN_TOKEN).getServerUrl());
	}

}
