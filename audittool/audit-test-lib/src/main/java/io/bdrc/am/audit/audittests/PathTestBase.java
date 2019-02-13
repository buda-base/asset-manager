package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.IAuditTest;
import io.bdrc.am.audit.iaudit.Outcome;

import java.io.File;

abstract public class PathTestBase extends AuditTestBase {
    /**
     * new AuditTestBase
     *
     * @param testName the test name
     */
    public PathTestBase(final String testName) {
        super(testName);
        SetParams(testName);
    }

    protected class NoDirTest implements ITestOperation {
        public void run() {
            File sourceDir = new File(getPath());

            // First test. Root must be a directory
            if (!sourceDir.isDirectory()) {
                FailTest(Outcome.ROOT_NOT_FOUND, getPath());
            }
        }
        public String getName() { return "No Directory Test";}
    }

    @Override
    abstract public void LaunchTest() ;

    /**
     * Hook for all subclasses to test base class
     */
    protected void RunBaseTests() {
            TestWrapper(new NoDirTest() );
    }

    public void SetParams(Object ... params) throws IllegalArgumentException {
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
    private String _path;
    // endregion
}
