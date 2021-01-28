package io.bdrc.assetmanager.testrun;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
     * @param auditTestResult audit tool Test Library test outcome
     */
    public TestRunResult(TestResult auditTestResult) {
        setTestRunOutcome(OutcomeMapper.AssetManagerOutcomeFromLibTestOutcome(auditTestResult.getOutcome()));
    }

    public Integer getTestRunOutcome() {
        return _testRunOutcome;
    }

    public void setTestRunOutcome(final Integer testRunOutcome) {
        _testRunOutcome = testRunOutcome;
    }

    private Integer _testRunOutcome ;
}
