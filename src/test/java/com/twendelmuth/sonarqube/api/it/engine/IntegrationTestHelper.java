package com.twendelmuth.sonarqube.api.it.engine;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;

public class IntegrationTestHelper {

	public static class SonarQubeVersionParameterResolver implements ParameterResolver {

		@Override
		public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
			return parameterContext.getParameter().getType().equals(SonarQubeVersion.class);
		}

		@Override
		public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
			List<SonarQubeVersion> version = new ArrayList<>();
			version.add(SonarQubeVersion.V9_LATEST_ENTERPRISE);
			version.add(SonarQubeVersion.V9_LATEST_ENTERPRISE);
			return version.stream();
		}

	}

}
