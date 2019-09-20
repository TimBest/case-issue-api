package gov.usds.case_issues.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import gov.usds.case_issues.test_util.CaseIssueApiTestBase;

@AutoConfigureMockMvc
@WithMockUser
public abstract class ControllerTestBase extends CaseIssueApiTestBase {

	protected static final String ORIGIN_HTTPS_OK = "https://ok-client.gov";
	protected static final String ORIGIN_HTTP_OK = "http://ok-client.net";
	protected static final String ORIGIN_NOT_OK = "http://evil-client.com";

	@Autowired
	private MockMvc _mvc;

	protected ResultActions perform(RequestBuilder requestBuilder) throws Exception {
		return _mvc.perform(requestBuilder);
	}

	protected ResultActions doGet(String url, String... origin) throws Exception {
		MockHttpServletRequestBuilder request = get(url);
		return addOriginAndPerform(request, origin);
	}

	protected ResultActions doGet(URI uri, String... origin) throws Exception {
		MockHttpServletRequestBuilder request = get(uri);
		return addOriginAndPerform(request, origin);
	}

	private ResultActions addOriginAndPerform(MockHttpServletRequestBuilder request, String... origin)
			throws Exception {
		return perform(addOrigin(request, origin));
	}

	protected MockHttpServletRequestBuilder addOrigin(MockHttpServletRequestBuilder request, String... origin) {
		if (origin.length == 1) {
			request.header("Origin", origin[0]);
		} else if (origin.length > 1) {
			throw new IllegalArgumentException("No stress testing the test harness!");
		}
		return request;
	}
}
