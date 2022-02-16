package io.github.twendelmuth.sonarqube.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.provider.ArgumentsSource;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(SonarQubeEnumArgumentsProvider.class)
public @interface SonarQubeVersionEnum {
	SonarQubeLicense license() default SonarQubeLicense.COMMUNITY;
}
