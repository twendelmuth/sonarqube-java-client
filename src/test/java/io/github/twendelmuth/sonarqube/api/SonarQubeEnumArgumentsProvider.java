package io.github.twendelmuth.sonarqube.api;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import io.github.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;

public class SonarQubeEnumArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<SonarQubeVersionEnum> {

	private SonarQubeVersionEnum enumSource;

	@Override
	public void accept(SonarQubeVersionEnum enumSource) {
		this.enumSource = enumSource;
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
		return SonarQubeVersion.getSonarQubeVersionsWithLicense(enumSource.license()).stream().map(Arguments::of);
	}

}
