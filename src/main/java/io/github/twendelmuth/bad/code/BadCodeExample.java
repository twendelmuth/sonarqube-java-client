package io.github.twendelmuth.bad.code;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;

public class BadCodeExample {

	private static final String SECURITY_TOKEN = ":abc";

	private String password = "myPassword";
	
	private String identifier = RandomStringUtils.randomAlphanumeric(24);

	public void badPractice() {
		ExecutorService executor = Executors.newSingleThreadExecutor();

		if (executor.isTerminated()) {

		}

		String unused = "test";
	}

	public String getPassword() {
		return password;
	}
	
	private static Matcher createMatcher(String styleRules) {
		Pattern p = Pattern.compile("\\s*" // ignore whitespace
				+ "([^{]+)" // any String that is not {
				+ "[{]" // the opening outer bracket
				+ "((?:[^{}]*(?:[{][^{}]*[}])?)*[^{}]*)" // nested { } (sequences of data and {.*}, and trailing data)
				+ "[}]", // the closing outer bracket
				Pattern.MULTILINE);

		return p.matcher(styleRules);
	}


}
