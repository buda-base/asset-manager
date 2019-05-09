package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.Outcome;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.*;


public class NoFoldersInImageGroups extends ImageGroupParents {

    /**
     * create test with external logger
     *
     * @param logger internal logger
     */
    public NoFoldersInImageGroups(Logger logger) {
        super("NoFoldersInImageGroups");
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
            } catch (DirectoryIteratorException die) {
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

            // Creating the filter. Bring in only directories
            DirectoryStream.Filter<Path> filter = entry -> (entry.toFile().isDirectory());

            try (DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(testFolder, filter)) {

                // iterate over directories in path
                boolean hasDirs = false;

                // This will only iterate if there are directories. Even hidden ones.
                for (Path entry : pathDirectoryStream) {

                    // Fail the test
                    if (!hasDirs) {
                        FailTest(Outcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER, testFolder.toString());
                        hasDirs = true;
                    }
                    FailTest(Outcome.DIR_IN_IMAGES_FOLDER, testFolder.toString(), entry.getFileName().toString());

                }
            } catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());
            }

        }
    }
}
