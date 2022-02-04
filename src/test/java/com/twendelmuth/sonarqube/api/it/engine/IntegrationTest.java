package com.twendelmuth.sonarqube.api.it.engine;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.platform.commons.annotation.Testable;

import com.twendelmuth.sonarqube.api.SonarQubeLicense;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Testable
@Tag("IntegrationTest")
public @interface IntegrationTest {
	SonarQubeLicense license() default SonarQubeLicense.COMMUNITY;

	boolean shouldStartServer() default true;
}
