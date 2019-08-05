package gov.usds.case_issues.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.usds.case_issues.db.model.projections.CaseSnoozeSummary;
import gov.usds.case_issues.model.CaseDetails;
import gov.usds.case_issues.model.SnoozeRequest;
import gov.usds.case_issues.services.CaseDetailsService;

@RestController
@RequestMapping("/api/caseDetails/{caseManagementSystemTag}/{receiptNumber}")
@CrossOrigin("http://localhost:3000")
public class CaseDetailsApiController {

	@Autowired
	private CaseDetailsService _caseDetailsService;

	@GetMapping
	public CaseDetails getCaseDetails(@PathVariable String caseManagementSystemTag, @PathVariable String receiptNumber) {
		return _caseDetailsService.findCaseDetails(caseManagementSystemTag, receiptNumber);
	}

	@GetMapping("activeSnooze")
	public ResponseEntity<CaseSnoozeSummary> getActiveSnooze(@PathVariable String caseManagementSystemTag, @PathVariable String receiptNumber) {
		Optional<CaseSnoozeSummary> snooze = _caseDetailsService.findActiveSnooze(caseManagementSystemTag, receiptNumber);
		return ResponseEntity.of(snooze);
	}

	@DeleteMapping("activeSnooze")
	public ResponseEntity<Void> endActiveSnooze(@PathVariable String caseManagementSystemTag, @PathVariable String receiptNumber) {
		// e-tag could be added here with the end-time of the snooze
		if (_caseDetailsService.endActiveSnooze(caseManagementSystemTag, receiptNumber)) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@PutMapping("activeSnooze")
	public ResponseEntity<CaseSnoozeSummary> changeActiveSnooze(
			@PathVariable String caseManagementSystemTag,
			@PathVariable String receiptNumber,
			@RequestBody @Valid SnoozeRequest requestedSnooze) {
		CaseSnoozeSummary replacement = _caseDetailsService.updateSnooze(caseManagementSystemTag, receiptNumber, requestedSnooze);
		return ResponseEntity.ok(replacement);
	}

}