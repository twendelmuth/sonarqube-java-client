package com.twendelmuth.sonarqube.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;;

class ScannerExtractionTest {

	@Test
	void scannerApp() {
		assertEquals("SonarSomething", SonarScannerExtractor.getSonarScannerApp("\t-\tsonar.scanner.app=SonarSomething\n"));
	}

	@Test
	void scannerApp_multiLine() {
		assertEquals("SonarSomething", SonarScannerExtractor.getSonarScannerApp("\n\n\t-\tsonar.scanner.app=SonarSomething\n"));
	}

	@Test
	void scannerApp_multiOccasions() {
		assertEquals("SonarSomething",
				SonarScannerExtractor.getSonarScannerApp("sonar.scanner.app=SonarSomething\n\n\t-\tsonar.scanner.app=SonarSomething2\n"));
	}

	@Test
	void scannerApp_notFound() {
		assertEquals("", SonarScannerExtractor.getSonarScannerApp(""));
	}

	@Test
	void scannerVersion() {
		assertEquals("1.0.0.0", SonarScannerExtractor.getSonarScannerVersion("\t-\tsonar.scanner.appVersion=1.0.0.0\n"));
	}

	@Test
	void scannerVersion_multiLine() {
		assertEquals("1.0.0.0", SonarScannerExtractor.getSonarScannerVersion("\n\n\t-\tsonar.scanner.appVersion=1.0.0.0\n"));
	}

	@Test
	void scannerVersion_multiOccasions() {
		assertEquals("1.0.0.0",
				SonarScannerExtractor.getSonarScannerVersion("sonar.scanner.appVersion=1.0.0.0\n\n\t-\tsonar.scanner.appVersion=2.0.0.0\n"));
	}

	@Test
	void scannerVersion_notFound() {
		assertEquals("", SonarScannerExtractor.getSonarScannerVersion(""));
	}

}
