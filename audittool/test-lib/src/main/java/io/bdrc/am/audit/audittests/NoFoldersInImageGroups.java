package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.LibOutcome;
import io.bdrc.am.audit.iaudit.Outcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import javax.xml.soap.Node;
import java.io.IOException;
import java.nio.file.*;


public class NoFoldersInImageGroups extends ImageGroupParents {


    public NoFoldersInImageGroups() {
        this(LoggerFactory.getLogger(NoFoldersInImageGroups.class));
    }

    public NoFoldersInImageGroups(final Logger logger) {
        this(logger,TestDictionary.NO_FILES_IN_FOLDER_TEST_NAME);
    }

    /**
     * create test with external logger
     *
     * @param logger internal logger
     * @param testName externally defined test name
     */
    public NoFoldersInImageGroups(Logger logger, final String testName) {
        super(testName);
        sysLogger = logger;
    }

    @Override
    public void LaunchTest() {
        // have base class tests here?
        // Yes, under the doctrine of One responsibility
        RunBaseTests();
        if (IsTestFailed()) {
            return;
        }
        TestWrapper(new NoFoldersInImageGroupsOperation());
    }

    public class NoFoldersInImageGroupsOperation implements ITestOperation {

        @Override
        public String getName() {
            return getTestName();
        }


        /**
         * Actual test content
         */
        @Override
        public void run() throws IOException {
            Path dir = Paths.get(getPath());

            // Creating the filter
            DirectoryStream.Filter<Path> filter = entry -> (entry.toFile().isDirectory());

            try (DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(dir, filter)) {

                // iterate over directories in path
                for (Path entry : pathDirectoryStream) {
                    // We only want to inspect specific directories
                    if (_imageGroupParents.contains(entry.getFileName().toString())) {

                        // Test the imagegroup for folders
                        DirectoryStream<Path> imageGroupDirStream = Files.newDirectoryStream(entry, filter);
                        for (Path imageGroup : imageGroupDirStream) {
                            testNoFolders(imageGroup);
                        }

                    }
                }
            }
            catch (NoSuchFileException nsfe)
            {
                String badPath = nsfe.getFile();
                sysLogger.error("No such file {}", badPath);
                FailTest(LibOutcome.ROOT_NOT_FOUND, badPath);
            }
            catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());
            }


            // Because we have a "non-set" state
            if (!IsTestFailed()) {
                PassTest();
            }
        }

        /**
         * Tests if a folder contains any folders
         *
         * @param testFolder Folder which should not contain any folders
         */
        private void testNoFolders(Path testFolder) throws IOException {

            String testFolderString = testFolder.toString();
            // Creating the filter. Bring in only directories
            DirectoryStream.Filter<Path> filter = entry -> (entry.toFile().isDirectory());

            try (DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(testFolder, filter)) {

                // iterate over directories in path
                boolean hasDirs = false;

                // This will only iterate if there are directories. Even hidden ones.
                for (Path entry : pathDirectoryStream) {

                    // Fail the test
                    if (!hasDirs) {
                        FailTest(LibOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER, testFolderString);
                        hasDirs = true;
                    }
                    FailTest(LibOutcome.DIR_IN_IMAGES_FOLDER, testFolderString, entry.getFileName().toString());
                }
            }
            catch (NoSuchFileException nsfe)
            {
                String badPath = nsfe.getFile();
                sysLogger.error("No such file {}", badPath);
                FailTest(LibOutcome.ROOT_NOT_FOUND, badPath);
            }
            catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());
            }

        }
    }
}
