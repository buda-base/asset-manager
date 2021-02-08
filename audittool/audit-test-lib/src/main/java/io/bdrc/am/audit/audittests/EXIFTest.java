package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.LibOutcome;
import io.bdrc.am.audit.iaudit.Outcome;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class EXIFTest  extends ImageGroupParents {


    /**
     * new AuditTestBase
     *
     * @param logger internal logger
     * @param testName caller names the test (mandatory - not like other classes with 1 arg constructors
     */
    public EXIFTest(Logger logger, String testName)
    {
        super(testName);
        sysLogger = logger;
        outcome = outcomeMap.get(testName);
    }

    /**
     * the name of the test determines the type of failure outcome
     * The outcome depends on the source directory. So user can warn about archive
     * failures, but hard fail on image failures
     */
    private final Integer outcome ;

    // The outcome depends on the source directory. So user can warn about archive
    // failures, but not
    // See TestDictionary.java - this is the last argument to AuditTestConfig
    private final Hashtable<String,Integer> outcomeMap =  new Hashtable<String,Integer>()
    {
        {
            put(TestDictionary.EXIF_ARCHIVE_TEST_NAME, LibOutcome.INVALID_ARCHIVE_EXIF);
            put(TestDictionary.EXIF_IMAGE_TEST_NAME, LibOutcome.INVALID_IMAGE_EXIF);
        }
    };

    public class EXIFTestOperation implements AuditTestBase.ITestOperation {

        @Override
        public String getName() {
            return getTestName();
        }

        @Override
        public void run() throws IOException {

            // Is there anything to do?
            if (!hasValidTargets(_imageGroupParents)) {
                PassTest();
                return;
            }

            // We only want directories in the _imageGroupParents entries
            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isDirectory()
                            && !entry.toFile().isHidden()
                            && _imageGroupParents.contains(entry.getFileName().toString()));

            try (DirectoryStream<Path> imageGroupParents = Files.newDirectoryStream(Paths.get(getPath()), filter)) {
                for (Path anImageGroupParent : imageGroupParents) {
                    MarkVisited(anImageGroupParent.getFileName().toString());
                    DirectoryStream.Filter<Path> imageGroupFilter =
                            entry -> (entry.toFile().isDirectory()
                                    && !entry.toFile().isHidden());

                    for (Path anImageGroup : Files.newDirectoryStream(anImageGroupParent, imageGroupFilter)) {
                        TestImages(anImageGroup);
                    }
                }
                // Because we have a "non-set" state
                if (!IsTestFailed()) {
                    PassTest();
                }
            } catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());
            }
            ReportUnvisited(sysLogger, false);
        }

        /**
         * EXIF Image Testing.
         * Loop over images in an image group to
         * @param imageGroupParent folder containing imageGroups
         * @throws IOException If io error
         */
        private void TestImages(final Path imageGroupParent) throws IOException {

            Integer outcome = outcomeMap.get(getTestName());

            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isFile()
                            && !(entry.toFile().isHidden()
                            || entry.toString().endsWith("json")));

            try (DirectoryStream<Path> imageFiles = Files.newDirectoryStream(imageGroupParent, filter)) {
                for (Path imageFile : imageFiles) {
                    File fileObject = imageFile.toAbsolutePath().toFile();
                    java.lang.String fileObjectPathString = imageFile.toAbsolutePath().toString();

                    try {
                        ImageEXIFAttributes exifAttrs = new ImageEXIFAttributes(fileObject);
                        List<ImageEXIFBead> invalidExifAttrs = validateEXIF(exifAttrs);
                        if (invalidExifAttrs.size() > 0) {
                            StringBuilder badTags = new StringBuilder();
                            invalidExifAttrs.forEach(x -> {
                                badTags.append(x.toString());
                                badTags.append(System.getProperty("line.separator"));
                            });
                            FailTest(outcome, fileObjectPathString, badTags.toString());
                        }
                    }
                    catch (UnsupportedFormatException ufe) {
                        FailTest(outcome, fileObjectPathString);
                    }

                }
            }
            sysLogger.debug("Test outcome {} error count {}", getTestResult().getOutcome(),
                    getTestResult()
                            .getErrors().size());
            if (!IsTestFailed()) {
                PassTest();
            }
        }

        /**
         * Validates an image EXIF data is acceptable.
         * @param imageExif Captured EXIF data
         * @return the list of EXIF attributes which failed.
         * Tests:
         * - Orientation, if present, must be "up" (0x1)
         */
        private List<ImageEXIFBead> validateEXIF(ImageEXIFAttributes imageExif) {
            List<ImageEXIFBead> failedEXIFs = new ArrayList<>();

            // Test for orientation. This is the only test so far
            imageExif.getExifAttributes().forEach( b -> {
                if (b.getTagNumber() == ImageEXIFAttributes.ORIENTATION_TAG) {
                    validOrientation(b,failedEXIFs);
                }

                // Add other tests here
            });
            return failedEXIFs;
        }

        /**
         * Test an EXIF attribute for validity
         * @param bead bead to test for rotation
         * @param outList existing list. Adds a failed rotation node to the output list.
         *
         */
        private void validOrientation(ImageEXIFBead bead, List<ImageEXIFBead> outList) {

            if (bead.getTagNumber() == ImageEXIFAttributes.ORIENTATION_TAG
                    && bead.getTagValue() != ImageEXIFAttributes.NO_EXIF_ROTATION_TAG
                    && bead.getTagValue() != ImageEXIFAttributes.EXIF_ROTATION_TAG_UP) {
                outList.add(bead);
            }
        }
    }

    @Override
    public void LaunchTest() {
        RunBaseTests();
        if (IsTestFailed()) {
            return;
        }
        TestWrapper(new EXIFTestOperation());
    }
}
