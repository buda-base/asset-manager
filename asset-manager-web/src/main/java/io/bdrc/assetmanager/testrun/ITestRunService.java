package io.bdrc.assetmanager.testrun;

import io.bdrc.assetmanager.config.Config;
import org.aspectj.weaver.ast.Test;

import java.util.List;
import java.util.Optional;

public interface ITestRunService {
    List<TestRun> getTestRuns() ;

    Optional<TestRun> getTestRunById(long id) ;

    TestRun updateTestRun( TestRun testRunDetails);

    TestRun addTestRun(TestRun newTestRun);

    List<TestRun> getUnStartedTestRuns();

    List<TestRun> getInProgressTestRuns();

    List<TestRun> getCompletedTestRuns();

    List<TestRun> getPassedTestRuns();

    List<TestRun> getFailedTestRuns();
}
