package io.bdrc.assetmanager.testrun;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.bdrc.assetmanager.config.SelectedTest;
import org.hibernate.sql.Select;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity which contains a test run. Contains
 * - Config
 * - Test Start Date/time
 * - Test End Date/time
 * - Test subject description (generally path)
 * - Summary of tests run
 * - individual test results
 */
@Entity
public class TestRun {

    @Id
    @GeneratedValue
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Date getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(final Date startDate) {
        this.startTimeStamp = startDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTimeStamp;

    public Date getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(final Date endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTimeStamp;

    public RunStatus getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(final RunStatus runStatus) {
        this.runStatus = runStatus;
    }

    @Enumerated(EnumType.ORDINAL)
    private RunStatus runStatus;

    public SelectedTest getTestSubject() {
        return testSubject;
    }

    public void setTestSubject(final SelectedTest testSubject) {
        this.testSubject = testSubject;
    }

    @OneToOne(targetEntity = SelectedTest.class,fetch = FetchType.EAGER)
    SelectedTest testSubject ;

    @OneToOne(targetEntity = TestRunResult.class,cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    TestRunResult _testRunResult;


    /**
     * Required Constructors
     */
    protected TestRun() {}

    /**
     * Copy constructor
     * @param source original test run
     */
    public TestRun(TestRun source)
    {
        this.runStatus = source.runStatus;
        this.endTimeStamp = source.endTimeStamp;
        this.startTimeStamp = source.startTimeStamp;
        this.id = source.id;
        this.testSubject = new SelectedTest(source.testSubject);
    }

}
