package com.twendelmuth.sonarqube.api.it.engine;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;

public class IntegrationTestEngineTestDescriptor extends AbstractTestDescriptor {

	private final Class<?> testClass;

	private final SonarQubeVersion sonarQubeVersion;

	private final boolean shouldStartServer;

	protected IntegrationTestEngineTestDescriptor(Class<?> testClass, UniqueId uniqueId, SonarQubeVersion sonarQubeVersion,
			boolean shouldStartServer) {
		super(uniqueId.append("class", testClass.getSimpleName()), testClass.getSimpleName(), ClassSource.from(testClass));
		this.testClass = testClass;
		this.sonarQubeVersion = sonarQubeVersion;
		this.shouldStartServer = shouldStartServer;
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	public Class<?> getTestClass() {
		return testClass;
	}

	public SonarQubeVersion getSonarQubeVersion() {
		return sonarQubeVersion;
	}

	public boolean isShouldStartServer() {
		return shouldStartServer;
	}

}
