package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

public abstract class AuditTestBase implements IAuditTest {

    /**
     * ITestOperation: functional interface
     */
    public interface ITestOperation {
        void run() throws IOException;
        String getName();
    }
    /**
     * new AuditTestBase
     *
     * @param testName the test name
     */
    AuditTestBase(String testName) {

        // in case caller forgets.
        // Base classes generally set logger from constructor
        sysLogger = LoggerFactory.getLogger(this.getClass());

        _testName = testName;
        _testResult = new TestResult();
        _testResult.setOutcome(Outcome.NOT_RUN);

        /*
         * use PropertyManager.getResourceAs{Int|String}(full name) to get resources
         *
         */
        // this.getClass() works because were in the same package (?)
        final ClassPropertyManager _propertyManager =
                new io.bdrc.am.audit.iaudit.ClassPropertyManager("/auditTool" +
                ".properties",getClass());
    }

    /**
     * Record a test instance failure.
     *
     * @param why           enum of outcome
     * @param failedElement element which failed test
     */
    void FailTest(Outcome why, String ...failedElement) {
        _testResult.setOutcome(Outcome.FAIL);
        _testResult.AddError(why, failedElement);
    }

    void PassTest() {
        _testResult.setOutcome(Outcome.PASS);
    }

    public boolean IsTestFailed() {
        return _testResult.getOutcome() == Outcome.FAIL;
    }

    public boolean IsTestPassed() {
        return _testResult.getOutcome() == Outcome.PASS;
    }

    // Public interface

    public TestResult getTestResult() {
        return _testResult;
    }

    public String getTestName() {
        return _testName;
    }

    /**
     * Wrap execution of a test interface
     * @param testOperation: the method which implements the test
     */
    void TestWrapper(ITestOperation testOperation) {
        try {
            // TODO: Create get logger name
            sysLogger.info(String.format("invoking test operation %s", testOperation.getName()));
            testOperation.run();
        } catch (Exception e) {

            // Records a special case: where the test did not run to completion,
            // so it neither passed nor failed.
            // This usually indicates a bug in the test or the environment
            getTestResult().setOutcome(Outcome.SYS_EXC);
            getTestResult().AddError(Outcome.SYS_EXC, e.toString());
        }
    }

    public void setLogger(Logger logger) {
        sysLogger = logger;
    }


    // IAuditTest interface

    /**
     * Subclasses must implement
     */
    public abstract void LaunchTest();

    /**
     * Set all test parameters (not logging or framework)
     * @param params implementation dependent optional parameters
     */
    public abstract void setParams(Object ... params );

    // region fields
    private TestResult _testResult;
    private String _testName;

    // package private implies most of protected
    Logger sysLogger;
    // endregion
}
