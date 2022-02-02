package com.twendelmuth.sonarqube.api.it.engine;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import com.twendelmuth.sonarqube.api.it.docker.SonarQubeVersion;

public class IntegrationSonarLicenseContainer extends AbstractTestDescriptor {

	private final SonarQubeVersion sonarQubeVersion;

	protected IntegrationSonarLicenseContainer(UniqueId uniqueId, SonarQubeVersion sonarQubeVersion) {
		super(uniqueId.append("version", sonarQubeVersion.name()), sonarQubeVersion.name());
		this.sonarQubeVersion = sonarQubeVersion;
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	public SonarQubeVersion getSonarQubeVersion() {
		return sonarQubeVersion;
	}

}
