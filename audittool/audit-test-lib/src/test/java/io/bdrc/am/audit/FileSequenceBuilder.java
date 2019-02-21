package io.bdrc.am.audit;

import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

class FileSequenceBuilder {

    private TemporaryFolder _rootFolder;

    FileSequenceBuilder(TemporaryFolder root) {
        _rootFolder = root;
    }

    private static int getArray(int[] arr, int pos, int defaultValue) {
        if (arr.length > (pos)) {
            return arr[pos];
        }
        return defaultValue;
    }

    /**
     * @param testRoot       Containing folder
     * @param intervalParams optional array of ints:
     *                       [0] is the number of files to create (default 12)
     *                       [1] is interval between file name numeric suffixes (default 1)
     *                       [2] is the number of each file to create (default 1)
     * @throws IOException if underlying Files calls fail
     */
    private static void GenerateEmptyFiles(final File testRoot, int... intervalParams) throws IOException {

        // Set up fail mode tests where file names have gaps

        int nFiles = getArray(intervalParams, 0, 12);
        int interval = getArray(intervalParams, 1, 1);
        int nEach = getArray(intervalParams, 2, 1);

        for (int j = 1; j < nFiles + 1; j++) {

            // dont create every file, for failure tests
            if ((j % interval) == 0) {
                String fileName = String.format("%04d.sfx", j);
                for (int each = 0; each < nEach; each++) {

                    //noinspection ResultOfMethodCallIgnored
                    File.createTempFile("file", fileName, testRoot);
                }
            }
        }
    }


    File BuildPassingFiles() throws IOException {

        for (int i = 0; i < 4; i++) {
            File testRoot = _rootFolder.newFolder(String.format("folder_%d", i));
            GenerateEmptyFiles(testRoot);

        }
        return _rootFolder.getRoot();
    }

    //
//    public File BuildNoFolders() {
//        return _rootFolder.getRoot();
//    }
//
    File BuildFilesOnly() throws IOException {
        File testRoot = _rootFolder.newFolder("test");
        GenerateEmptyFiles(testRoot);
        return testRoot;
    }


    File BuildDuplicateFiles() throws IOException {

        for (int i = 0; i < 4; i++) {
            File testRoot = _rootFolder.newFolder(String.format("folder_%d", i));

            // build folders with missing files
            GenerateEmptyFiles(testRoot, 12, 1, 2);

        }
        return _rootFolder.getRoot();
    }

    File BuildMissingFiles() throws IOException {

        for (int i = 0; i < 4; i++) {
            File testRoot = _rootFolder.newFolder(String.format("folder_%d", i));

            // build folders with missing files
            GenerateEmptyFiles(testRoot, 12, 2);
        }
        return _rootFolder.getRoot();
    }

}
