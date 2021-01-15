package io.bdrc.assetmanager.testrun;

import io.bdrc.assetmanager.config.Config;
import org.aspectj.weaver.ast.Test;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestRunService implements ITestRunService {

    private final TestRunRepository _testRunRepository;

    public TestRunService(final TestRunRepository testRunRepository) {
        _testRunRepository = testRunRepository;
    }

    @Override
    public List<TestRun> getTestRuns() {
        return (List<TestRun>) _testRunRepository.findAll();
    }

    @Override
    public Optional<TestRun> getTestRunById(final long id) {
        return _testRunRepository.findById(id);
    }

    @Override
    public TestRun updateTestRun(final TestRun modTestRun) {
        TestRun newTestRun;
        if (modTestRun.getId() == null || modTestRun.getId() == 0) throw new EntityNotFoundException("TestRun");

        Optional<TestRun> origTestRun = _testRunRepository.findById(modTestRun.getId());
        if (origTestRun.isPresent()) {
            newTestRun = new TestRun(modTestRun);
        } else throw new EntityNotFoundException(String.format("Test Run with id %d not found."
                , modTestRun.getId()));
        return _testRunRepository.save(newTestRun);

    }

    @Override
    public TestRun addTestRun(final TestRun newTestRun) {
        return _testRunRepository.save(newTestRun);
    }

    @Override
    public List<TestRun> getUnStartedTestRuns() {
        return getRunsByRunStatus(RunStatus.NOT_RUN);
    }

    @Override
    public List<TestRun> getInProgressTestRuns() {
        return getRunsByRunStatus(RunStatus.STARTED);
    }

    @Override
    public List<TestRun> getCompletedTestRuns() {
        return getRunsByRunStatus(RunStatus.COMPLETED);
    }

    /**
     * TODO: Test Run results
     * @return
     */
    @Override
    public List<TestRun> getPassedTestRuns() {
        return getCompletedTestRuns();
    }

    @Override
    public List<TestRun> getFailedTestRuns() {
        return null;
    }

    private List<TestRun> getRunsByRunStatus(RunStatus runStatus) {
        return Lists.newArrayList(_testRunRepository.findAll())
                .stream()
                .filter(x -> x.getRunStatus() == runStatus)
                .collect(Collectors.toList());
    }
}
