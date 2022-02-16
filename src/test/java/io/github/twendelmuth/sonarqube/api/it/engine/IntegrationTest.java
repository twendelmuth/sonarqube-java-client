package io.github.twendelmuth.sonarqube.api.it.engine;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.platform.commons.annotation.Testable;

import io.github.twendelmuth.sonarqube.api.SonarQubeLicense;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Testable
public @interface IntegrationTest {
	SonarQubeLicense license() default SonarQubeLicense.COMMUNITY;

	boolean shouldStartServer() default true;
}
