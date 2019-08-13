package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.Outcome;

import java.io.File;
import java.util.ArrayList;

/**
 *
 */
abstract public class PathTestBase extends AuditTestBase {
    /**
     * new AuditTestBase
     *
     * @param testName the test name
     */
    PathTestBase(final String testName) {
        super(testName);
    }

    protected class NoDirTest implements ITestOperation {
        public void run() {
            File sourceDir = new File(getPath());

            // First test. Root must be a directory
            if (!sourceDir.isDirectory()) {
                FailTest(LibOutcome.ROOT_NOT_FOUND, getPath());
            }
        }
        public String getName() { return "No Directory Test";}
    }

    @Override
    abstract public void LaunchTest() ;

    /**
     * Hook for all subclasses to test base class
     */
    void RunBaseTests() {
            TestWrapper(new NoDirTest() );
    }

    /**
     * Every subclass of AuditTestBase implements their own
     * parameter set.
     * @param params array of parameters, implementation dependent
     * @throws IllegalArgumentException when the input is null
     */
    public void setParams(Object ... params) throws IllegalArgumentException {
        if ((params == null) || (params.length < 1)){
            throw new IllegalArgumentException("Directory to test required, but not given.");
        }

        // For this class, the only thing we care about is the path
        _path = params[0].toString();
    }

    // region properties

    public String getPath() {
        return _path;
    }

    // endregion

    // region property fields
    protected String _path;
    // endregion

}
