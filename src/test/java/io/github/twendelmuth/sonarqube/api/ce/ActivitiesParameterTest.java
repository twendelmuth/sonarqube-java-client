package io.github.twendelmuth.sonarqube.api.ce;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import io.github.twendelmuth.sonarqube.api.ce.ActivitiesParameter.ActivitiesStatus;
import io.github.twendelmuth.sonarqube.api.ce.ActivitiesParameter.ActivitiesType;

class ActivitiesParameterTest {
	private ZonedDateTime time = ZonedDateTime.ofInstant(LocalDateTime.of(2022, Month.JANUARY, 1, 1, 1, 1, 0), ZoneOffset.UTC, ZoneId.of("Z"));

	@Test
	void noParams() {
		assertEquals("", ActivitiesParameter.builder().build().toParameterString());
	}

	@Test
	void component() {
		assertEquals("?component=component1",
				ActivitiesParameter.builder()
						.component("component1")
						.build().toParameterString());
	}

	@Test
	void maxExecutedAt() {
		assertEquals("?maxExecutedAt=2022-01-01T01:01:01%2B0000",
				ActivitiesParameter.builder()
						.maxExecutedAt(time)
						.build().toParameterString());
	}

	@Test
	void minSubmittedAt() {
		assertEquals("?minSubmittedAt=2022-01-01T01:01:01%2B0000",
				ActivitiesParameter.builder()
						.minSubmittedAt(time)
						.build().toParameterString());
	}

	@Test
	void onlyCurrents() {
		assertEquals("?onlyCurrents=false",
				ActivitiesParameter.builder()
						.onlyCurrents(false)
						.build().toParameterString());
	}

	@Test
	void pageSize() {
		assertEquals("?ps=25",
				ActivitiesParameter.builder()
						.pageSize(25)
						.build().toParameterString());
	}

	@Test
	void pageSize_escape0() {
		assertEquals("?ps=1",
				ActivitiesParameter.builder()
						.pageSize(0)
						.build().toParameterString());
	}

	@Test
	void pageSize_escape1001() {
		assertEquals("?ps=1000",
				ActivitiesParameter.builder()
						.pageSize(1001)
						.build().toParameterString());
	}

	@Test
	void query() {
		assertEquals("?q=Apache",
				ActivitiesParameter.builder()
						.query("Apache")
						.build().toParameterString());
	}

	@Test
	void status() {
		assertEquals("?status=SUCCESS",
				ActivitiesParameter.builder()
						.addStatus(ActivitiesStatus.SUCCESS)
						.build().toParameterString());
	}

	@Test
	void statusMultiple() {
		assertEquals("?status=FAILED,CANCELED",
				ActivitiesParameter.builder()
						.addStatus(ActivitiesStatus.FAILED)
						.addStatus(ActivitiesStatus.CANCELED)
						.build().toParameterString());
	}

	@Test
	void type() {
		assertEquals("?type=APP_REFRESH",
				ActivitiesParameter.builder()
						.type(ActivitiesType.APP_REFRESH)
						.build().toParameterString());
	}

	@Test
	void allWithQuery() {
		assertEquals(
				"?maxExecutedAt=2022-01-01T01:01:01%2B0000&minSubmittedAt=2022-01-01T01:01:01%2B0000&onlyCurrents=false&ps=30&q=Apache&status=SUCCESS,FAILED,CANCELED,PENDING,IN_PROGRESS&type=REPORT",
				ActivitiesParameter.builder()
						.maxExecutedAt(time)
						.minSubmittedAt(time)
						.onlyCurrents(false)
						.pageSize(30)
						.query("Apache")
						.addStatus(ActivitiesStatus.SUCCESS)
						.addStatus(ActivitiesStatus.FAILED)
						.addStatus(ActivitiesStatus.CANCELED)
						.addStatus(ActivitiesStatus.PENDING)
						.addStatus(ActivitiesStatus.IN_PROGRESS)
						.type(ActivitiesType.REPORT)
						.build().toParameterString());
	}

	@Test
	void allWithComponent() {
		assertEquals(
				"?component=c1&maxExecutedAt=2022-01-01T01:01:01%2B0000&minSubmittedAt=2022-01-01T01:01:01%2B0000&onlyCurrents=false&ps=30&status=SUCCESS,FAILED,CANCELED,PENDING,IN_PROGRESS&type=REPORT",
				ActivitiesParameter.builder()
						.component("c1")
						.maxExecutedAt(time)
						.minSubmittedAt(time)
						.onlyCurrents(false)
						.pageSize(30)
						.addStatus(ActivitiesStatus.SUCCESS)
						.addStatus(ActivitiesStatus.FAILED)
						.addStatus(ActivitiesStatus.CANCELED)
						.addStatus(ActivitiesStatus.PENDING)
						.addStatus(ActivitiesStatus.IN_PROGRESS)
						.type(ActivitiesType.REPORT)
						.build().toParameterString());
	}

}
