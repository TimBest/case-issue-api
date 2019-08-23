package gov.usds.case_issues.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser
public class SystemNavigationControllerTest extends ControllerTestBase {

	@Before
	public void resetDb() {
		truncateDb();
	}

	@Test
	public void getNavigationInformation_noData_emptyList() throws Exception {
		doGet("/api/navigation")
			.andExpect(status().isOk())
			.andExpect(content().json("[]", true))
			;
	}

	@Test
	public void getNavigationInformation_trivialData_singleItemList() throws Exception {
		_dataService.ensureCaseManagementSystemInitialized("YO", "Your case manager");
		_dataService.ensureCaseTypeInitialized("W2", "Income Reporting", "That form you get every January");
		doGet("/api/navigation")
			.andExpect(status().isOk())
			.andExpect(content().json("[{\"tag\": \"YO\", \"name\": \"Your case manager\", "
					+ "\"caseTypes\": [{\"tag\":\"W2\", \"name\": \"Income Reporting\","
					+ " \"description\": \"That form you get every January\"}]}]"))
			;

	}
}
