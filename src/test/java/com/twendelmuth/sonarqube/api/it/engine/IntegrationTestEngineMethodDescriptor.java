package com.twendelmuth.sonarqube.api.it.engine;

import java.lang.reflect.Method;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

public class IntegrationTestEngineMethodDescriptor extends AbstractTestDescriptor {

	private final Method javaMethod;

	protected IntegrationTestEngineMethodDescriptor(Method javaMethod, UniqueId uniqueId) {
		super(uniqueId.append("class", javaMethod.getName()), javaMethod.getName(), MethodSource.from(javaMethod));
		this.javaMethod = javaMethod;
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

	public Method getJavaMethod() {
		return javaMethod;
	}

}
