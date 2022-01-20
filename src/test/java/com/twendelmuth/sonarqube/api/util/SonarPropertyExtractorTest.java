package com.twendelmuth.sonarqube.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;;

class SonarPropertyExtractorTest {

	@Test
	void scannerApp() {
		assertEquals("SonarSomething", SonarPropertyExtractor.getSonarScannerApp("\t-\tsonar.scanner.app=SonarSomething\n"));
	}

	@Test
	void scannerApp_multiLine() {
		assertEquals("SonarSomething", SonarPropertyExtractor.getSonarScannerApp("\n\n\t-\tsonar.scanner.app=SonarSomething\n"));
	}

	@Test
	void scannerApp_multiOccasions() {
		assertEquals("SonarSomething",
				SonarPropertyExtractor.getSonarScannerApp("sonar.scanner.app=SonarSomething\n\n\t-\tsonar.scanner.app=SonarSomething2\n"));
	}

	@Test
	void scannerApp_notFound() {
		assertEquals("", SonarPropertyExtractor.getSonarScannerApp(""));
	}

	@Test
	void scannerVersion() {
		assertEquals("1.0.0.0", SonarPropertyExtractor.getSonarScannerVersion("\t-\tsonar.scanner.appVersion=1.0.0.0\n"));
	}

	@Test
	void scannerVersion_multiLine() {
		assertEquals("1.0.0.0", SonarPropertyExtractor.getSonarScannerVersion("\n\n\t-\tsonar.scanner.appVersion=1.0.0.0\n"));
	}

	@Test
	void scannerVersion_multiOccasions() {
		assertEquals("1.0.0.0",
				SonarPropertyExtractor.getSonarScannerVersion("sonar.scanner.appVersion=1.0.0.0\n\n\t-\tsonar.scanner.appVersion=2.0.0.0\n"));
	}

	@Test
	void scannerVersion_notFound() {
		assertEquals("", SonarPropertyExtractor.getSonarScannerVersion(""));
	}

}
