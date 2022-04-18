package io.bdrc.audit.audittests;

import io.bdrc.audit.iaudit.IAuditTest;
import io.bdrc.audit.iaudit.LibOutcome;
import io.bdrc.audit.iaudit.Outcome;
import io.bdrc.audit.iaudit.TestResult;
import io.bdrc.audit.iaudit.message.LibTestMessages;
import io.bdrc.audit.iaudit.message.TestMessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

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

    }

    /**
     * Record a test instance failure.
     *
     * @param why           enum of outcome
     * @param failedElement element which failed test
     */
    protected void FailTest(Integer why, String... failedElement) {

        // filter out some errors.
        // For filtered errors, leave the state as is

        // jimk asset-manager-162: Set warning when ErrorsAsWarnings set
        _testResult.setOutcome(_passableErrors.contains(why) ? Outcome.WARN : Outcome.FAIL);
        _testResult.AddError(why, failedElement);
    }

    void MarkTestNotRun(Integer why, String... failedElement) {
        _testResult.setOutcome(Outcome.NOT_RUN);
        _testResult.AddError(why, failedElement);
    }

    void PassTest() {
        _testResult.setOutcome(Outcome.PASS);
    }

    public boolean IsTestFailed() {
        return _testResult.getOutcome().equals(Outcome.FAIL)
                || _testResult.getOutcome().equals(Outcome.WARN)
                ;
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
     *
     * @param testOperation: the method which implements the test
     */
    void TestWrapper(ITestOperation testOperation) {
        try {
            // TODO: Create get logger name
            sysLogger.trace(String.format("invoking test operation %s", testOperation.getName()));
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
     * Set test parameters needed in base class
     *
     * @param params implementation dependent optional parameters
     */
    public void setParams(Object... params)
    {
        // All tests may need some parameters. They can extract parameters from this dictionary
        if (params.length > 1) {
            // Unchecked /
            keywordArgParams = (Hashtable<String, String>) params[1];
        }
        // See shell.properties
        // name of the property which holds the list of errors which are not considered
        // fatal.
        final String errorsWarningsPropertyKey = "ErrorsAsWarning";
        parseLoad(errorsWarningsPropertyKey, _passableErrors);
    }

    // region fields
    private final TestResult _testResult;
    private final String _testName;

    private final LinkedList<Integer> _passableErrors = new LinkedList<>();

    // package private implies most of protected
    Logger sysLogger;
    // endregion

    // region TestParameter properties
    Hashtable<String, String> keywordArgParams = new Hashtable<>();

//    /**
//     * transform inbound parameters from KWArg format (key=value) to
//     * HashDictionary<>(key, value)</>
//     *
//     * @param kwparams keyword parameters
//     *                 Saves results in protected testParameters field.
//     */
//    protected final void LoadKeywordParams(String[] kwparams) {
//        Arrays.stream(kwparams).forEach((String s) -> {
//            String[] de = s.split("=");
//            if (s.length() > 1)
//            {
//                keywordArgParams.put(de[0], de[1]);
//            }
//        });
//    }

    /**
     * Test message specific to this library. Assigned into the IAudit messages
     * in AuditTestBase constructor. It's central so that each test can share other test outcomes
     */
    private static final Hashtable<Integer, TestMessageFormat> libTestMessages =
            new Hashtable<>() {{
                put(LibOutcome.ROOT_NOT_FOUND, new TestMessageFormat(1, "Path %s is not a directory or does not" +
                        " exist."));
                put(LibOutcome.FILES_IN_MAIN_FOLDER, new TestMessageFormat(2, "Root folder %s contains file %s"));
                put(LibOutcome.DIR_IN_IMAGES_FOLDER, new TestMessageFormat(2, "Image group folder %s  contains " +
                        "directory %s"));
                put(LibOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER, new TestMessageFormat(1, "Image group folder %s  fails " +
                        "files only test."));
                put(LibOutcome.FILE_SEQUENCE, new TestMessageFormat(1, "Sequence %s not found"));
                put(LibOutcome.DIR_FAILS_SEQUENCE, new TestMessageFormat(1, "Folder %s fails sequence test."));
                put(LibOutcome.DUP_SEQUENCE, new TestMessageFormat(2, "Duplicate Sequence %s and %s found"));
                put(LibOutcome.DUP_SEQUENCE_FOLDER, new TestMessageFormat(1, "Folder %s contains Duplicate Sequences"));
                put(LibOutcome.FILE_COUNT, new TestMessageFormat(3, "Folder %s expected %s files in folder , found %s"));
                put(LibOutcome.NO_IMAGE_READER, new TestMessageFormat(1, "Image file %s has no suitable reader."));
                put(LibOutcome.INVALID_TIFF, new TestMessageFormat(2, "Image file %s is invalid TIFF. Reasons: %s "));
                put(LibOutcome.FILE_SIZE, new TestMessageFormat(3, "Image file %s size %s exceeds maximum of %s"));
                put(LibOutcome.BAD_FILE_SIZE_ARG, new TestMessageFormat(1, "Invalid file size argument %s. Requires " +
                        "n[K|M|G]"));
                put(LibOutcome.INVALID_ARCHIVE_EXIF, new TestMessageFormat(2,
                        "Archive Image file %s contains invalid EXIF tags: %s"));
                put(LibOutcome.INVALID_IMAGE_EXIF, new TestMessageFormat(2,
                        "Image file %s contains invalid EXIF tags: %s"));
                put(LibOutcome.INVALID_ARCHIVE_THUMBNAIL, new TestMessageFormat(1,
                        "Archive Image file %s contains thumbnail"));
                put(LibOutcome.INVALID_IMAGE_THUMBNAIL, new TestMessageFormat(1,
                        "Archive Image file %s contains thumbnail"));

            }};

    /**
     * Extract a list of integers from a keyword value
     *
     * @param name name pf property holding integers
     * @param dest target of assignment
     */
    private void parseLoad(String name, final List<Integer> dest) {


        String kwname = keywordArgParams.getOrDefault(name, "");
        try {
            // Special case parameter
            String[] pEArray = kwname.split(",");
            Arrays.stream(pEArray).filter(x -> !x.isEmpty()).forEach(

                    x -> dest.add(Integer.parseInt(x.trim()))
            );
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException(String.format("Property %s, value %s is not a list of Integers.", name,
                    kwname));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}


