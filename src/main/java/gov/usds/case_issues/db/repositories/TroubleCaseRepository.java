package gov.usds.case_issues.db.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import gov.usds.case_issues.db.model.CaseManagementSystem;
import gov.usds.case_issues.db.model.CaseType;
import gov.usds.case_issues.db.model.TroubleCase;

@RepositoryRestResource(
	path="cases",
	itemResourceRel="case",
	collectionResourceRel="cases",
	collectionResourceDescription=@Description("All cases that have had at least one issue reported.")
)
public interface TroubleCaseRepository extends PagingAndSortingRepository<TroubleCase, Long>, BulkCaseRepository {

	public static final String ACTIVE_CASE_CLAUSE = "c.caseManagementSystem = :caseManagementSystem and c.caseType = :caseType and c.openIssues is not empty";
	public static final String ACTIVE_CASE_QUERY = "select c from #{#entityName} c where " + ACTIVE_CASE_CLAUSE;
	public static final String ACTIVE_SNOOZE_CLAUSE = "exists (select snoozeEnd from CaseSnooze where snoozeCase = c and snoozeEnd > CURRENT_TIMESTAMP)";

	public Page<TroubleCase> getAllByCaseManagementSystemAndCaseType(CaseManagementSystem caseManager, CaseType caseType, Pageable pageable);

	public Optional<TroubleCase> findByCaseManagementSystemAndReceiptNumber(CaseManagementSystem caseManager, String receiptNumber);

	@Query(ACTIVE_CASE_QUERY)
	public Page<TroubleCase> getWithOpenIssues(CaseManagementSystem caseManagementSystem, CaseType caseType, Pageable pageable);

	@Query(ACTIVE_CASE_QUERY + " and not " + ACTIVE_SNOOZE_CLAUSE)
	public Page<TroubleCase> getAwakeWithOpenIssues(CaseManagementSystem caseManagementSystem, CaseType caseType, Pageable pageable);

	// we will want to fetch the snooze info in a join: need to figure out how DTO works
	@Query(ACTIVE_CASE_QUERY + " and " + ACTIVE_SNOOZE_CLAUSE)
	public Page<TroubleCase> getSnoozedWithOpenIssues(CaseManagementSystem caseManagementSystem, CaseType caseType, Pageable pageable);

}
