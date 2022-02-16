package io.github.twendelmuth.sonarqube.api.it.engine;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.platform.commons.annotation.Testable;

import io.github.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;

/**
 * Marker annotation that denotes an Integration Test case we should run.
 * Method needs to have a {@link SonarQubeVersion} parameter.
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
@Testable
public @interface ITest {

}
