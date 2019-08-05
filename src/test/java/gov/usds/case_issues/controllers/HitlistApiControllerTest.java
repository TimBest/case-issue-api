package gov.usds.case_issues.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import gov.usds.case_issues.db.model.CaseManagementSystem;
import gov.usds.case_issues.db.model.CaseType;
import gov.usds.case_issues.db.model.TroubleCase;
import gov.usds.case_issues.test_util.FixtureDataInitializationService;
import gov.usds.case_issues.test_util.HsqlDbTruncator;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HitlistApiControllerTest {

	private static final String VALID_CASE_TYPE = "C1";
	private static final String VALID_CASE_MGT_SYS = "F1";
	private static final String API_PATH = "/api/cases/{caseManagementSystemTag}/{caseTypeTag}/";
	private static final String CASE_TYPE_NOPE = "Case Type 'NOPE' was not found";
	private static final String CASE_MANAGEMENT_SYSTEM_NOPE = "Case Management System 'NOPE' was not found";

	@Autowired
	private FixtureDataInitializationService dataService;
	@Autowired
	private HsqlDbTruncator truncator;
	@Autowired
	private MockMvc mvc;

	private CaseManagementSystem _system;
	private CaseType _type;

	@Before
	public void resetDb() {
		truncator.truncateAll();
		_system = dataService.ensureCaseManagementSystemInitialized(VALID_CASE_MGT_SYS, "Fake 1", "Fakest");
		_type = dataService.ensureCaseTypeInitialized(VALID_CASE_TYPE, "Case type 1", "");
	}

	@Test
	public void invalidPath_correctErrorMessages() throws Exception {
		mvc.perform(getActive("NOPE", VALID_CASE_TYPE))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_MANAGEMENT_SYSTEM_NOPE))
		;
		mvc.perform(getSnoozed("NOPE", VALID_CASE_TYPE))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_MANAGEMENT_SYSTEM_NOPE))
		;
		mvc.perform(getSummary("NOPE", VALID_CASE_TYPE))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_MANAGEMENT_SYSTEM_NOPE))
		;
		mvc.perform(getActive(VALID_CASE_MGT_SYS, "NOPE"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_TYPE_NOPE))
		;
		mvc.perform(getSnoozed(VALID_CASE_MGT_SYS, "NOPE"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_TYPE_NOPE))
		;
		mvc.perform(getSummary(VALID_CASE_MGT_SYS, "NOPE"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_TYPE_NOPE))
		;
	}

	@Test
	public void validPath_noData_emptyResponses() throws Exception {
		mvc.perform(getActive(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("[]", true))
		;
		mvc.perform(getSnoozed(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("[]", true))
		;
		mvc.perform(getSummary(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("{}", true))
		;
	}

	@Test
	public void getActive_withData_correctResponse() throws Exception {
		initCaseData();
		mvc.perform(getSummary(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("{'NEVER_SNOOZED': 1, 'CURRENTLY_SNOOZED': 1}", true))
		;
		mvc.perform(getActive(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("[{'receiptNumber': 'FFFF1111', 'previouslySnoozed': false}]", false))
		;
		mvc.perform(getSnoozed(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("[{'receiptNumber': 'FFFF1112', 'snoozeInformation': {'snoozeReason': 'DONOTCARE'}}]", false))
		;
	}

	/**
	 * Create some data on our default case type!
	 * 
	 * 1 case that has 1 issue and is currently active
	 * 1 case that has 1 issue and is currently snoozed
	 */
	private void initCaseData() {
		ZonedDateTime thatWasThen = ZonedDateTime.now().minusMonths(1);
		TroubleCase case1 = dataService.initCase(_system, "FFFF1111", _type, thatWasThen);
		dataService.initIssue(case1, "FOOBAR", thatWasThen, null);
		TroubleCase case2 = dataService.initCase(_system, "FFFF1112", _type, thatWasThen);
		dataService.initIssue(case2, "FOOBAR", thatWasThen, null);
		dataService.snoozeCase(case2);
	}

	private static MockHttpServletRequestBuilder getActive(String cmsTag, String ctTag) {
		return get(API_PATH + "active", cmsTag, ctTag);
	}

	private static MockHttpServletRequestBuilder getSnoozed(String cmsTag, String ctTag) {
		return get(API_PATH + "snoozed", cmsTag, ctTag);
	}

	private static MockHttpServletRequestBuilder getSummary(String cmsTag, String ctTag) {
		return get(API_PATH + "summary", cmsTag, ctTag);
	}

}
