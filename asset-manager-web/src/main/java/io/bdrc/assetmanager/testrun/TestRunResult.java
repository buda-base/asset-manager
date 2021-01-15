package io.bdrc.assetmanager.testrun;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.bdrc.am.audit.iaudit.*;

@Entity
public class TestRunResult {

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @GeneratedValue
    @Id
    Long id;

    @OneToOne(targetEntity = TestRun.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private TestRun _testRun;

    public TestRunResult() {}

    /**
     * Construct from audit tool
     * @param auditTestResult
     */
    public TestRunResult(TestResult auditTestResult) {
        setTestRunOutcome(OutcomeMapper.fromOutcome(auditTestResult.getOutcome()));

    }

    public TestRunOutcome getTestRunOutcome() {
        return _testRunOutcome;
    }

    public void setTestRunOutcome(final TestRunOutcome testRunOutcome) {
        _testRunOutcome = testRunOutcome;
    }

    private TestRunOutcome _testRunOutcome ;
}
