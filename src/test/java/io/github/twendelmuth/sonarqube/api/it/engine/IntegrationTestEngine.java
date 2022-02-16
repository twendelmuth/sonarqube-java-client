package io.github.twendelmuth.sonarqube.api.it.engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import io.github.twendelmuth.sonarqube.api.it.docker.SonarQubeDockerContainer;
import io.github.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;

public class IntegrationTestEngine implements TestEngine {

	private IntegrationSettings integrationSettings = new IntegrationSettings();

	@Override
	public String getId() {
		return "SonarQubeIntegrationTestEngine";
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
		Map<SonarQubeVersion, TestDescriptor> sonarQubeEngineMap = new HashMap<>();

		EngineDescriptor rootNode = new EngineDescriptor(uniqueId, getId());

		discoveryRequest.getSelectorsByType(ClasspathRootSelector.class)
				.forEach(selector -> appendTestsByClasspath(sonarQubeEngineMap, rootNode, selector));
		discoveryRequest.getSelectorsByType(PackageSelector.class)
				.forEach(selector -> appendTestsInPackage(sonarQubeEngineMap, rootNode, selector.getPackageName()));
		discoveryRequest.getSelectorsByType(ClassSelector.class)
				.forEach(selector -> appendTestsInClass(sonarQubeEngineMap, rootNode, selector.getJavaClass()));
		discoveryRequest.getSelectorsByType(MethodSelector.class)
				.forEach(selector -> appendTestsOfMethod(sonarQubeEngineMap, rootNode, selector.getJavaMethod()));

		return rootNode;
	}

	private List<SonarQubeVersion> getSonarQubeVersions(IntegrationTest integrationTest) {
		return SonarQubeVersion.getSonarQubeVersionsWithLicense(integrationSettings.getAvailableSonarQubeVersions(), integrationTest.license());
	}

	private void appendTestsOfMethod(Map<SonarQubeVersion, TestDescriptor> sonarQubeEngineMap, EngineDescriptor rootNode, Method javaMethod) {
		if (isTestMethod().test(javaMethod)) {
			appendTestMethods(sonarQubeEngineMap, rootNode, javaMethod.getDeclaringClass(), javaMethod);
		}
	}

	private TestDescriptor getLicenseContainer(Map<SonarQubeVersion, TestDescriptor> sonarQubeEngineMap, EngineDescriptor rootNode,
			SonarQubeVersion sonarQubeVersion) {
		return sonarQubeEngineMap.computeIfAbsent(sonarQubeVersion, qubeVersion -> {
			IntegrationSonarLicenseContainer licContainer = new IntegrationSonarLicenseContainer(rootNode.getUniqueId(), qubeVersion);
			rootNode.addChild(licContainer);
			return licContainer;
		});

	}

	private TestDescriptor findClassContainerInLicenseContainer(TestDescriptor licenseContainer, Class<?> javaClass, SonarQubeVersion version,
			boolean shouldStartServer) {
		Optional<? extends TestDescriptor> testClassOptional = licenseContainer.getChildren().stream()
				.filter(testContainer -> testContainer instanceof IntegrationTestEngineTestDescriptor &&
						((IntegrationTestEngineTestDescriptor) testContainer).getTestClass().equals(javaClass))
				.findFirst();

		if (testClassOptional.isPresent()) {
			return testClassOptional.get();
		} else {
			TestDescriptor testContainer = new IntegrationTestEngineTestDescriptor(javaClass,
					licenseContainer.getUniqueId(), version, shouldStartServer);
			licenseContainer.addChild(testContainer);
			return testContainer;
		}

	}

	private void appendTestMethods(Map<SonarQubeVersion, TestDescriptor> sonarQubeEngineMap, EngineDescriptor rootNode, Class<?> javaClass,
			Method javaMethod) {
		if (isTestClass().test(javaClass) && isTestMethod().test(javaMethod)) {
			IntegrationTest integrationTest = javaClass.getAnnotation(IntegrationTest.class);

			getSonarQubeVersions(integrationTest).forEach(version -> {
				TestDescriptor licenseContainer = getLicenseContainer(sonarQubeEngineMap, rootNode, version);
				TestDescriptor testContainer = findClassContainerInLicenseContainer(licenseContainer, javaClass, version,
						integrationTest.shouldStartServer());
				testContainer.addChild(new IntegrationTestEngineMethodDescriptor(javaMethod, testContainer.getUniqueId()));
			});
		}

	}

	private List<SonarQubeVersion> getVersionsForClass(Class<?> javaClass) {
		if (isTestClass().test(javaClass)) {
			IntegrationTest integrationTest = javaClass.getAnnotation(IntegrationTest.class);

			return SonarQubeVersion.getSonarQubeVersionsWithLicense(integrationTest.license());
		}

		return Collections.EMPTY_LIST;
	}

	private void appendTestsInClass(Map<SonarQubeVersion, TestDescriptor> sonarQubeEngineMap, EngineDescriptor rootNode, Class<?> javaClass) {
		if (isTestClass().test(javaClass)) {
			ReflectionUtils.findMethods(javaClass, isTestMethod())
					.forEach(testMethod -> appendTestMethods(sonarQubeEngineMap, rootNode, javaClass, testMethod));
		}
	}

	private void appendTestsInPackage(Map<SonarQubeVersion, TestDescriptor> sonarQubeEngineMap, EngineDescriptor rootNode, String packageName) {
		ReflectionUtils.findAllClassesInPackage(packageName, isTestClass(), (name) -> true)
				.forEach(javaClass -> appendTestsInClass(sonarQubeEngineMap, rootNode, javaClass));
	}

	private void appendTestsByClasspath(Map<SonarQubeVersion, TestDescriptor> sonarQubeEngineMap, EngineDescriptor rootNode,
			ClasspathRootSelector selector) {
		URI classpathRoot = selector.getClasspathRoot();
		ReflectionUtils.findAllClassesInClasspathRoot(classpathRoot, isTestClass(), (name) -> true)
				.forEach(javaClass -> appendTestsInClass(sonarQubeEngineMap, rootNode, javaClass));
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor testDescriptor = request.getRootTestDescriptor();
		EngineExecutionListener listener = request.getEngineExecutionListener();

		listener.executionStarted(testDescriptor);
		testDescriptor.getChildren().forEach(licenseContainer -> {
			if (licenseContainer instanceof IntegrationSonarLicenseContainer) {
				try {
					executeSonarLicenseContainer(listener, (IntegrationSonarLicenseContainer) licenseContainer);
					listener.executionFinished(testDescriptor, TestExecutionResult.successful());
				} catch (Exception e) {
					listener.executionFinished(testDescriptor, TestExecutionResult.failed(e));
				}
			}
		});

	}

	private void executeSonarLicenseContainer(EngineExecutionListener listener, IntegrationSonarLicenseContainer container) {
		SonarQubeVersion version = container.getSonarQubeVersion();

		container.getChildren().forEach(testDescriptor -> {

			if (testDescriptor instanceof IntegrationTestEngineTestDescriptor) {
				try {
					listener.executionStarted(testDescriptor);
					executeTestClass(version, listener, (IntegrationTestEngineTestDescriptor) testDescriptor);
				} catch (Exception e) {
					listener.executionFinished(testDescriptor,
							TestExecutionResult.failed(e));
				}
			} else {
				listener.executionStarted(testDescriptor);
				listener.executionFinished(testDescriptor, TestExecutionResult.failed(new AssertionError("Not sure how to handle, not executed.")));
			}
		});
	}

	private void executeTestClass(SonarQubeVersion version, EngineExecutionListener listener,
			IntegrationTestEngineTestDescriptor testDescriptor) {
		listener.executionStarted(testDescriptor);

		boolean serverStarted = false;
		AtomicBoolean keepRunning = new AtomicBoolean(true);

		testDescriptor.getChildren().forEach(testMethod -> {
			if (testMethod instanceof IntegrationTestEngineMethodDescriptor) {
				if (!keepRunning.get()) {
					listener.executionFinished(testMethod,
							TestExecutionResult.failed(new AssertionError("Couldn't start docker containers")));
					return;
				}

				if (!serverStarted && testDescriptor.isShouldStartServer()) {
					try {
						SonarQubeDockerContainer.build(version).startSonarQubeContainer();
					} catch (Exception e) {
						listener.executionFinished(testMethod, TestExecutionResult.failed(e));
						keepRunning.set(false);
						return;
					}
				}

				Object classUnderTest = ReflectionUtils.newInstance(testDescriptor.getTestClass());
				executeTestMethod(version, classUnderTest, listener,
						(IntegrationTestEngineMethodDescriptor) testMethod);
			} else {
				listener.executionStarted(testMethod);
				listener.executionFinished(testMethod,
						TestExecutionResult.failed(new AssertionError("Not sure how to handle this test, not executed")));
			}
		});

		if (serverStarted) {
			SonarQubeDockerContainer.build(version).stopSonarQubeContainer();
		}
		listener.executionFinished(testDescriptor, TestExecutionResult.successful());

	}

	private void executeTestMethod(SonarQubeVersion version, Object testClass, EngineExecutionListener listener,
			IntegrationTestEngineMethodDescriptor methodDescriptor) {
		listener.executionStarted(methodDescriptor);

		boolean isSuccess = true;

		try {
			runMethodsOnTestClass(testClass, ReflectionUtils.findMethods(testClass.getClass(), isMethodWithAnnotation(BeforeEach.class)));
			if (methodDescriptor.getJavaMethod().getParameterCount() == 1
					&& methodDescriptor.getJavaMethod().getParameters()[0].getType().isAssignableFrom(SonarQubeVersion.class)) {
				ReflectionUtils.invokeMethod(methodDescriptor.getJavaMethod(), testClass, version);
			} else {
				throw new UnsupportedOperationException("Method needs to have only one parameter of type SonarQubeVersion");
			}

		} catch (Throwable e) {
			isSuccess = false;
			listener.executionFinished(methodDescriptor, TestExecutionResult.failed(e));
		}

		try {
			runMethodsOnTestClass(testClass, ReflectionUtils.findMethods(testClass.getClass(), isMethodWithAnnotation(AfterEach.class)));
		} catch (Throwable e) {
			isSuccess = false;
			listener.executionFinished(methodDescriptor, TestExecutionResult.failed(e));
		}

		if (isSuccess) {
			listener.executionFinished(methodDescriptor, TestExecutionResult.successful());
		}
	}

	private void runMethodsOnTestClass(Object testClass, List<Method> methodsToRun) {
		methodsToRun.forEach(method -> ReflectionUtils.invokeMethod(method, testClass));
	}

	private static Predicate<Class<?>> isTestClass() {
		return classCandidate -> AnnotationSupport.isAnnotated(classCandidate, IntegrationTest.class);
	}

	private static Predicate<Class<?>> isTestClass(EngineDiscoveryRequest discoveryRequest) {
		//		Filter.composeFilters(discoveryRequest.getFiltersByType(null))
		return classCandidate -> AnnotationSupport.isAnnotated(classCandidate, IntegrationTest.class);
	}

	private static Predicate<Method> isTestMethod() {
		return method -> {
			if (ReflectionUtils.isStatic(method) || ReflectionUtils.isPrivate(method) || ReflectionUtils.isAbstract(method)) {
				return false;
			}

			return method.getAnnotation(ITest.class) != null;
		};
	}

	private static <T extends Annotation> Predicate<Method> isMethodWithAnnotation(Class<T> annotationClass) {
		return method -> {
			if (ReflectionUtils.isStatic(method) || ReflectionUtils.isAbstract(method)) {
				return false;
			}

			return method.getAnnotation(annotationClass) != null;
		};
	}

}
