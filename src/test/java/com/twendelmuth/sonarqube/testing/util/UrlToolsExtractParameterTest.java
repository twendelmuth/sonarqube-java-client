package com.twendelmuth.sonarqube.testing.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;

import org.junit.jupiter.api.Test;

class UrlToolsExtractParameterTest {

	@Test
	void testNullParameter() {
		Map<String, String> result = UrlTools.extractQueryParameterMap(null);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	void testSimpleParameters() {
		Map<String, String> result = UrlTools.extractQueryParameterMap("https://localhost?key1=value1&key2=value2");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.containsKey("key1"));
		assertTrue(result.containsKey("key2"));
		assertTrue(result.containsValue("value1"));
		assertTrue(result.containsValue("value2"));
	}

	@Test
	void testMultipleAnds() {
		Map<String, String> result = UrlTools.extractQueryParameterMap("https://localhost?key1=value1&key2=value2&key3=value3");
		assertNotNull(result);
		assertEquals(3, result.size());
		assertTrue(result.containsKey("key1"));
		assertTrue(result.containsKey("key2"));
		assertTrue(result.containsKey("key3"));
		assertTrue(result.containsValue("value1"));
		assertTrue(result.containsValue("value2"));
		assertTrue(result.containsValue("value3"));
	}

	@Test
	void testEmptysParameter() {
		Map<String, String> result = UrlTools.extractQueryParameterMap("https://localhost?key1");
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.containsKey("key1"));
	}

	@Test
	void testMultipleSameKeys_notSupported() {
		Map<String, String> result = UrlTools.extractQueryParameterMap("https://localhost?key1=value1&key1=value2");
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.containsKey("key1"));
		assertTrue(result.containsValue("value2"));
	}

	@Test
	void testSemicolon() {
		Map<String, String> result = UrlTools.extractQueryParameterMap("https://localhost;session=123?key1=value1&key2=value2");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertFalse(result.containsKey("session"));
		assertFalse(result.containsValue("123"));
	}

}
