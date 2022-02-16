package io.github.twendelmuth.sonarqube.api.it.engine;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.platform.engine.TestTag;
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

	@Override
	public Set<TestTag> getTags() {
		Set<TestTag> allTags = new HashSet<>();
		getParent().ifPresent(parentDescriptor -> allTags.addAll(parentDescriptor.getTags()));
		return allTags;
	}

}
