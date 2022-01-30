package com.twendelmuth.sonarqube.api;

import java.util.ArrayList;
import java.util.List;

public class NameValuePair {
	private final String name;

	private final String value;

	public NameValuePair(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public static List<NameValuePair> listOf(String key1, String value1) {
		List<NameValuePair> list = new ArrayList<>();
		list.add(new NameValuePair(key1, value1));
		return list;
	}

	public static List<NameValuePair> listOf(String key1, String value1, String key2, String value2) {
		List<NameValuePair> list = new ArrayList<>();
		list.add(new NameValuePair(key1, value1));
		list.add(new NameValuePair(key2, value2));
		return list;
	}

}
