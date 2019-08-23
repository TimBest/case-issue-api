package gov.usds.case_issues.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import gov.usds.case_issues.db.model.CaseManagementSystem;
import gov.usds.case_issues.db.model.CaseType;
import gov.usds.case_issues.db.model.TroubleCase;

@WithMockUser(username = "default_hitlist_user", authorities = "READ_CASES")
public class HitlistApiControllerTest extends ControllerTestBase {

	private static final String VALID_CASE_TYPE = "C1";
	private static final String VALID_CASE_MGT_SYS = "F1";
	private static final String API_PATH = "/api/cases/{caseManagementSystemTag}/{caseTypeTag}/";
	private static final String CASE_TYPE_NOPE = "Case Type 'NOPE' was not found";
	private static final String CASE_MANAGEMENT_SYSTEM_NOPE = "Case Management System 'NOPE' was not found";

	private CaseManagementSystem _system;
	private CaseType _type;

	@Before
	public void resetDb() {
		truncateDb();
		_system = _dataService.ensureCaseManagementSystemInitialized(VALID_CASE_MGT_SYS, "Fake 1", "Fakest");
		_type = _dataService.ensureCaseTypeInitialized(VALID_CASE_TYPE, "Case type 1", "");
	}

	@Test
	public void invalidPath_correctErrorMessages() throws Exception {
		perform(getActive("NOPE", VALID_CASE_TYPE))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_MANAGEMENT_SYSTEM_NOPE))
		;
		perform(getSnoozed("NOPE", VALID_CASE_TYPE))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_MANAGEMENT_SYSTEM_NOPE))
		;
		perform(getSummary("NOPE", VALID_CASE_TYPE))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_MANAGEMENT_SYSTEM_NOPE))
		;
		perform(getActive(VALID_CASE_MGT_SYS, "NOPE"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_TYPE_NOPE))
		;
		perform(getSnoozed(VALID_CASE_MGT_SYS, "NOPE"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_TYPE_NOPE))
		;
		perform(getSummary(VALID_CASE_MGT_SYS, "NOPE"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("message").value(CASE_TYPE_NOPE))
		;
	}

	@Test
	public void validPath_noData_emptyResponses() throws Exception {
		perform(getActive(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("[]", true))
		;
		perform(getSnoozed(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("[]", true))
		;
		perform(getSummary(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("{}", true))
		;
	}

	@Test
	public void getActive_withData_correctResponse() throws Exception {
		initCaseData();
		perform(getSummary(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("{'NEVER_SNOOZED': 1, 'CURRENTLY_SNOOZED': 1}", true))
		;
		perform(getActive(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("[{'receiptNumber': 'FFFF1111', 'previouslySnoozed': false}]", false))
		;
		perform(getSnoozed(VALID_CASE_MGT_SYS, VALID_CASE_TYPE))
			.andExpect(status().isOk())
			.andExpect(content().json("[{'receiptNumber': 'FFFF1112', 'snoozeInformation': {'snoozeReason': 'DONOTCARE'}}]", false))
		;
	}

	@Test
	@WithMockUser(authorities = "UPDATE_ISSUES")
	public void putJson_emptyList_accepted() throws Exception {
		MockHttpServletRequestBuilder jsonPut = put("/api/cases/{caseManagementSystemTag}/{caseTypeTag}/{issueTag}", VALID_CASE_MGT_SYS, VALID_CASE_TYPE, "WONKY")
			.contentType(MediaType.APPLICATION_JSON)
			.content("[]");
		perform(jsonPut).andExpect(status().isAccepted());
	}

	@Test
	@WithMockUser(authorities = "UPDATE_ISSUES")
	public void putCsv_emptyList_accepted() throws Exception {
		MockHttpServletRequestBuilder jsonPut = put("/api/cases/{caseManagementSystemTag}/{caseTypeTag}/{issueTag}", VALID_CASE_MGT_SYS, VALID_CASE_TYPE, "WONKY")
			.contentType("text/csv")
			.content("header1,header2\n");
		perform(jsonPut).andExpect(status().isAccepted());
	}

	/**
	 * Create some data on our default case type!
	 * 
	 * 1 case that has 1 issue and is currently active
	 * 1 case that has 1 issue and is currently snoozed
	 */
	private void initCaseData() {
		ZonedDateTime thatWasThen = ZonedDateTime.now().minusMonths(1);
		TroubleCase case1 = _dataService.initCase(_system, "FFFF1111", _type, thatWasThen);
		_dataService.initIssue(case1, "FOOBAR", thatWasThen, null);
		TroubleCase case2 = _dataService.initCase(_system, "FFFF1112", _type, thatWasThen);
		_dataService.initIssue(case2, "FOOBAR", thatWasThen, null);
		_dataService.snoozeCase(case2);
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
