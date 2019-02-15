package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.Outcome;
import org.slf4j.Logger;

import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * NoFilesInRoot tests that, for a Standard Submission Structure (
 * see https://buda-base.github.io/digitization-guidelines/#standards/standards-en/
 * which contains 'n' files, the
 * file names end with the sequence 1..n (dont care about leading prefixes)
 * and all have the same file extension
 */
public class NoFilesInRoot extends PathTestBase  {

    // region constructors

    /**
     * Tests for no files in root directory.
     *
     */
    public NoFilesInRoot(Logger logger)
    {
        super("NoFilesInRoot");
        sysLogger = logger;
    }
    //endregion

    /**
     * Internal class to pass to TestWrapper.
     * note base class must pass its test (such as directory exists)
     */
    public class  NoFilesInDirTest implements ITestOperation {

        public String getName() { return getTestName();}
        public void run() throws java.io.IOException {

            // Directory must have nothing in it but directories

            // Creating the filter
            DirectoryStream.Filter<Path> filter = entry -> !(entry.toFile().isHidden());

            Path dir = Paths.get(getPath());
            try (DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(dir, filter)) {

                for (Path entry : pathDirectoryStream) {
                    if (!Files.isDirectory(entry)) {
                        FailTest(Outcome.FILES_IN_MAIN_FOLDER, getPath(),entry.toString());
                        // return;
                    }
                }
            } catch (DirectoryIteratorException die) {
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());
            }

            // Because we have a "non-set" state
            if (!IsTestFailed()) {
                PassTest();
            }
        }
    }


    /**
     * Test implementation
     */
    public void LaunchTest() {

        // have base class tests here?
        // Yes, under the doctrine of One responsibility
        RunBaseTests();
        if (IsTestFailed()) {
            return;
        }
        TestWrapper( new NoFilesInDirTest());
    }

}
