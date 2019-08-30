package io.bdrc.am.audit;

import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Creates a work structure for file sequence to test. Sequence test requires:
 * Top folder/
 * 0 Or More arbitraryfolder names
 * 1 Or More folder names which are "parents of Image groups"
 * These folders are passed into the test by the SequenceTest.setParams() method.
 */
class FileSequenceBuilder {

    private final TemporaryFolder _rootFolder;
    private final Collection<String> _imageGroupParentFolders;

    FileSequenceBuilder(TemporaryFolder root, Collection<String> imageGroupParentFolders) {
        _rootFolder = root;
        _imageGroupParentFolders = imageGroupParentFolders;
    }


    FileSequenceBuilder(TemporaryFolder root) {
        _rootFolder = root;
        _imageGroupParentFolders = new ArrayList<>();
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

        String[] fileNameLengths = {
                "_%d.sfx",
                "_%01d.sfx",
                "_%02d.sfx",
                "_%03d.sfx",
                "_%04d.sfx",
        };
        Integer fileNameLengthsIndex = 0;

        for (int j = 1; j < nFiles + 1; j++) {


            // dont create every file, for failure tests
            if ((j % interval) == 0) {

                // For asset-manager-7: create random length file names
                fileNameLengthsIndex = nextFileNameLengthIndex(fileNameLengths, fileNameLengthsIndex);
                String fileName = String.format(fileNameLengths[fileNameLengthsIndex], j);
                for (int each = 0; each < nEach; each++) {

                    //noinspection ResultOfMethodCallIgnored
                    File.createTempFile("file", fileName, testRoot);
                }
            }
        }
    }

    File BuildPassingFiles() throws IOException {

        // Create unsearched folders
        for (int i = 0; i < imageGroupsPerParent(); i++) {
            File testRoot = _rootFolder.newFolder(String.format("folder_%d", i));
            GenerateEmptyFiles(testRoot);
        }

        for (int i = 0; i < imageGroupsPerParent(); i++) {
            File testRoot = _rootFolder.newFolder(String.format("folder2_%d", i));
            GenerateEmptyFiles(testRoot, 5, 3, 2);
        }

        BuildMissingFiles();

        return _rootFolder.getRoot();
    }


    File BuildFileSequencePassingFiles() throws IOException {
        File testRoot = BuildPassingFiles();

        DirectoryStream.Filter<Path > directoryOnlyFilter =
                entry -> (entry.toFile().isDirectory());

        // add a random directory to some of the image groups under the image group parents.
        for (String igp : _imageGroupParentFolders) {
            Path igpPath = Paths.get(testRoot.getAbsolutePath(),igp);

            // Get the imagegroup folders
            try (DirectoryStream<Path> imageGroups = Files.newDirectoryStream(igpPath, directoryOnlyFilter))
            {
                for (Path ig : imageGroups)
                {
                    Files.createDirectory(Paths.get(ig.toAbsolutePath().toString(),"Mostly Harmless"));
                }
            }

        }
        return _rootFolder.getRoot();
    }

    File BuildFilesOnly() throws IOException {
        File testRoot = _rootFolder.newFolder("test");
        GenerateEmptyFiles(testRoot);
        return testRoot;
    }


    File BuildMissingFiles(int... fillParameters) throws IOException {

        for (String igp : _imageGroupParentFolders) {
            File testRoot = _rootFolder.newFolder(igp);
            // build folders with missing files
            BuildImageGroups(testRoot, fillParameters);
        }
        return _rootFolder.getRoot();
    }

    void BuildImageGroups(File rootFolder, int... fillParams) throws IOException {
        for (int i = 0; i < imageGroupsPerParent(); i++) {
            File ig =
                    Files.createDirectory(Paths.get(rootFolder.getAbsolutePath(), String.format("folder_%d", i))).toFile();
            GenerateEmptyFiles(ig, fillParams);
        }
    }

    private static Integer nextFileNameLengthIndex(String []fileNameLengths, int fileNameLengthsIndex) {
        if (++fileNameLengthsIndex == fileNameLengths.length) {
            fileNameLengthsIndex = 0;
        }
        return fileNameLengthsIndex;
    }

    @SuppressWarnings("SameReturnValue")
    public int imageGroupsPerParent() {
        return 4;
    }

}
