package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.*;

import io.bdrc.am.audit.iaudit.message.LibTestMessages;
import io.bdrc.am.audit.iaudit.message.TestMessageFormat;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

import java.util.Arrays;
import java.util.Hashtable;

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

        // Load the library's test messages
        LibTestMessages.getInstance().setMessages(libTestMessages);

//        /*
//         * use PropertyManager.getResourceAs{Int|String}(full name) to get resources
//         *
//         */
//        // this.getClass() works because were in the same package (?)
//        final ClassPropertyManager _propertyManager =
//                new io.bdrc.am.audit.iaudit.ClassPropertyManager("/auditTool" +
//                ".properties",getClass());
    }

    /**
     * Record a test instance failure.
     *
     * @param why           enum of outcome
     * @param failedElement element which failed test
     */
    void FailTest(Integer why, String ...failedElement) {
        _testResult.setOutcome(Outcome.FAIL);
        _testResult.AddError(why, failedElement);
    }


    void PassTest() {
        _testResult.setOutcome(Outcome.PASS);
    }

    public boolean IsTestFailed() {
        return _testResult.getOutcome().equals( Outcome.FAIL);
    }

    public boolean IsTestPassed() {
        return _testResult.getOutcome().equals(Outcome.PASS);
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
    private final TestResult _testResult;
    private final String _testName;

    // package private implies most of protected
    Logger sysLogger;
    // endregion

    // region TestParameter properties
    protected Hashtable<String,String> testParameters = new Hashtable<>();

    /**
     * transform inbound parameters from KWArg format (key=value) to
     * HashDictionary<>(key, value)</>
     * @param kwparams keyword parameters
     */
    protected final void LoadParameters(String[]kwparams) {
        Arrays.stream(kwparams).forEach( (String s ) -> {
            String [] de = s.split("=");
            if (s.length() >1 ) {
                testParameters.put(de[0],de[1]);
            }
        });

    }
    /**
     * Test message specific to this library. Assigned into the IAudit messages
     * in AuditTestBase constructor. It's central so that each test can share other test outcomes
     */
    public static final Hashtable<Integer, TestMessageFormat> libTestMessages =
    new Hashtable<Integer, TestMessageFormat>()
            {{
                put(LibOutcome.ROOT_NOT_FOUND, new TestMessageFormat(1, "Path %s is not a directory or does not exist."));
                put(LibOutcome.FILES_IN_MAIN_FOLDER,  new TestMessageFormat(2,"Root folder %s contains file %s"));
                put(LibOutcome.DIR_IN_IMAGES_FOLDER,  new TestMessageFormat(2,"Image group folder %s  contains " +
                        "directory %s"));
                put(LibOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER,  new TestMessageFormat(1,"Image group folder %s  fails " +
                        "files only test."));
                put(LibOutcome.FILE_SEQUENCE, new TestMessageFormat(1, "Sequence %s not found"));
                put(LibOutcome.DIR_FAILS_SEQUENCE, new TestMessageFormat(1, "Folder %s fails sequence test."));
                put(LibOutcome.DUP_SEQUENCE,  new TestMessageFormat(2,"Duplicate Sequence %s and %s found"));
                put(LibOutcome.DUP_SEQUENCE_FOLDER, new TestMessageFormat(1, "Folder %s contains Duplicate Sequences"));
                put(LibOutcome.FILE_COUNT,  new TestMessageFormat(3,"Folder %s expected %s files in folder , found %s"));
                put(LibOutcome.NO_IMAGE_READER, new TestMessageFormat(1,"Image file %s has no suitable reader."));
                put(LibOutcome.INVALID_TIFF, new TestMessageFormat(2,"Image file %s is invalid TIFF. Reasons: %s "));
                put(LibOutcome.FILE_SIZE, new TestMessageFormat(3,"Image file %s size %s exceeds maximum of %s" +
                        "invalid " +
                        "TIFF. Reasons: %s"));

            }};
}
