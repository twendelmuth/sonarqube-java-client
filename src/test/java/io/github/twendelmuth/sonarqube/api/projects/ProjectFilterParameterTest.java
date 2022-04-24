package io.github.twendelmuth.sonarqube.api.projects;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import io.github.twendelmuth.sonarqube.api.projects.ProjectFilterParameter.ProjectQualifier;

class ProjectFilterParameterTest {
	private LocalDate localDateInThePast() {
		return LocalDate.of(2022, Month.JANUARY, 1);
	}

	private ZonedDateTime zonedDateTimeInThePast() {
		return ZonedDateTime.ofInstant(LocalDateTime.of(2022, Month.JANUARY, 1, 1, 1, 1, 0), ZoneOffset.UTC, ZoneId.of("Z"));
	}

	@Test
	void analyzedBeforeWithTime() {
		ProjectFilterParameter filter = ProjectFilterParameter.bulkDeleteProjectFilterBuilder()
				.analyzedBefore(zonedDateTimeInThePast())
				.build();

		assertEquals("?analyzedBefore=2022-01-01T01:01:01%2B0000", filter.toParameterString());
	}

	@Test
	void analyzedBeforeNoTime() {
		ProjectFilterParameter filter = ProjectFilterParameter.bulkDeleteProjectFilterBuilder()
				.analyzedBefore(localDateInThePast())
				.build();

		assertEquals("?analyzedBefore=2022-01-01", filter.toParameterString());
	}

	@Test
	void onProvisionedOnly() {
		ProjectFilterParameter filter = ProjectFilterParameter.bulkDeleteProjectFilterBuilder()
				.provisionedOnly(true)
				.build();

		assertEquals("?onProvisionedOnly=true", filter.toParameterString());
	}

	@Test
	void projects() {
		ProjectFilterParameter filter = ProjectFilterParameter.bulkDeleteProjectFilterBuilder()
				.addProjectKey("my-project")
				.build();

		assertEquals("?projects=my-project", filter.toParameterString());
	}

	@Test
	void projectsMultiple() {
		ProjectFilterParameter filter = ProjectFilterParameter.bulkDeleteProjectFilterBuilder()
				.addProjectKey("my-project")
				.addProjectKey("my-project2")
				.build();

		assertEquals("?projects=my-project,my-project2", filter.toParameterString());
	}

	@Test
	void query() {
		ProjectFilterParameter filter = ProjectFilterParameter.bulkDeleteProjectFilterBuilder()
				.query("Apache")
				.build();

		assertEquals("?q=Apache", filter.toParameterString());
	}

	@Test
	void qualifiers() {
		ProjectFilterParameter filter = ProjectFilterParameter.bulkDeleteProjectFilterBuilder()
				.addQualifier(ProjectQualifier.TRK)
				.build();

		assertEquals("?qualifiers=TRK", filter.toParameterString());
	}

	@Test
	void qualifiersMultiple() {
		ProjectFilterParameter filter = ProjectFilterParameter.bulkDeleteProjectFilterBuilder()
				.addQualifier(ProjectQualifier.APP)
				.addQualifier(ProjectQualifier.TRK)
				.build();

		assertEquals("?qualifiers=APP,TRK", filter.toParameterString());
	}

	@Test
	void allParameters_testBulkDeleteFilter() {
		ProjectFilterParameter filter = ProjectFilterParameter.bulkDeleteProjectFilterBuilder()
				.addProjectKey("my-project")
				.addQualifier(ProjectQualifier.APP)
				.analyzedBefore(zonedDateTimeInThePast())
				.query("äbc")
				.provisionedOnly(true)
				.build();

		String parameterString = filter.toParameterString();

		String expectedParameterString = "?analyzedBefore=2022-01-01T01:01:01%2B0000&onProvisionedOnly=true&projects=my-project&q=äbc&qualifiers=APP";
		assertEquals(expectedParameterString, parameterString);
	}

	@Test
	void page() {
		ProjectFilterParameter filter = ProjectFilterParameter.searchProjectFilterBuilder().page(1).build();
		assertEquals("?p=1", filter.toParameterString());
	}

	@Test
	void page_negative() {
		ProjectFilterParameter filter = ProjectFilterParameter.searchProjectFilterBuilder().page(-1).build();
		assertEquals("", filter.toParameterString());
	}

	@Test
	void pageSize() {
		ProjectFilterParameter filter = ProjectFilterParameter.searchProjectFilterBuilder().pageSize(250).build();
		assertEquals("?ps=250", filter.toParameterString());
	}

	@Test
	void pageSize_escape0() {
		ProjectFilterParameter filter = ProjectFilterParameter.searchProjectFilterBuilder().pageSize(0).build();
		assertEquals("?ps=1", filter.toParameterString());
	}

	@Test
	void pageSize_escape500() {
		ProjectFilterParameter filter = ProjectFilterParameter.searchProjectFilterBuilder().pageSize(501).build();
		assertEquals("?ps=500", filter.toParameterString());
	}

	@Test
	void enoughParameters() {
		assertAll(
				() -> assertTrue(ProjectFilterParameter.searchProjectFilterBuilder().analyzedBefore(localDateInThePast()).build()
						.hasEnoughParametersForBulkDelete()),
				() -> assertTrue(ProjectFilterParameter.searchProjectFilterBuilder().analyzedBefore(zonedDateTimeInThePast()).build()
						.hasEnoughParametersForBulkDelete()),
				() -> assertTrue(ProjectFilterParameter.searchProjectFilterBuilder().addProjectKey("some-project").build()
						.hasEnoughParametersForBulkDelete()),
				() -> assertTrue(ProjectFilterParameter.searchProjectFilterBuilder().query("some-query").build()
						.hasEnoughParametersForBulkDelete()));
	}

	@Test
	void notEnoughParameters() {
		assertFalse(ProjectFilterParameter.searchProjectFilterBuilder().build().hasEnoughParametersForBulkDelete());
	}

}
