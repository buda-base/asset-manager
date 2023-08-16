package io.bdrc.audit.audittests;

import io.bdrc.audit.iaudit.LibOutcome;
import io.bdrc.audit.iaudit.Outcome;
import io.bdrc.audit.iaudit.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ImageFileNameFormatTest - guarantees an image name corresponds to the image group
 * component of its containing directory. The formal definition of a correct file name format is
 * - disk version of the image group name
 * - optional separator: one of - or _
 * - four digits (controlled by the property FileSequence.SequenceLength)
 * -
 * Ex:
 * ImageGroup W1KG421234/images/1KG421234-I1KG31412559 contains files:
 * I1KG314125590001.jpg
 * I1KG31412559_0002.jpg
 * W1KG421234-I1KG31412559_0003.jpg
 * I1KG31412559-0004.jpg
 * <p>
 * Files
 *      I1KG314125590001.jpg
 *      I1KG31412559_0002.jpg
 *      I1KG31412559-0004.jpg
 *  will pass this test,
 * File  W1KG421234-I1KG31412559_0003.jpg will not
 * The test validates the image group name as follows:
 * [[ :alphanum: ]]+-[[:alphanum:]]+ will use the  suffix (after the - separator) as the image group name
 * [[ :alphanum: ]]+ will just use the contents of the image group directory name
 */
public class ImageFileNameFormatTest extends ImageGroupParents {

    public ImageFileNameFormatTest() {
        this(LoggerFactory.getLogger(ImageFileNameFormatTest.class));
    }

    public ImageFileNameFormatTest(Logger logger) {
        this(logger, TestDictionary.IMAGE_FILENAME_FORMAT);
    }

    private final int _platformSequenceLength;


    public ImageFileNameFormatTest(Logger logger, String testName)
    {
        super(testName);
        sysLogger = logger;
        _platformSequenceLength = PropertyManager.getInstance().getPropertyInt("FileSequence.SequenceLength");
    }

    @Override
    public void LaunchTest() {
            TestWrapper(new ImageFileNameFormatTestOperation());
    }

    public class ImageFileNameFormatTestOperation implements AuditTestBase.ITestOperation {
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
            } catch (
                    DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());
            }

            ReportUnvisited(sysLogger, false);

        }

        /**
         * Test the image files in a directory. See class dfn for filename correctness
         * @param imageGroup Path to image group directory
         */
        private void TestImages(final Path imageGroup) {
            String igNameString = imageGroup.getFileName().toString();
            String[] igNames = igNameString.split("-");

            if ( igNames.length < 2 )
                throw new IllegalArgumentException(String.format("Image group path must be tokens separated by - %s",
                        igNameString));

            // Create a regex for the image group name
            // Compile once, use many
            Pattern igRegex =Pattern.compile(String.format("%s[-_]{0,1}\\d{%s}\\..*",igNames[1],
                    _platformSequenceLength), Pattern.CASE_INSENSITIVE);

            // Use after the split, if any
            String targetIgName = igNames[1];



            // filter in files that are not hidden and not json and not passing the test
            DirectoryStream.Filter<Path> invalidImageFilter =
                    entry -> (entry.toFile().isFile()
                            && !(entry.toFile().isHidden()
                            || entry.toString().endsWith("json"))
                            && !ImagePassesTest(igRegex, entry.getFileName().toString())

                    );

            try (DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(imageGroup, invalidImageFilter)) {

                // iterate over directories in path
                for (Path entry : pathDirectoryStream) {
                    FailTest(LibOutcome.INVALID_IMAGE_FILENAME_FORMAT,entry.toString(), targetIgName);
                }

                // Because we have a "non-set" state
                if (!IsTestFailed()) {
                    PassTest();
                }
            } catch (DirectoryIteratorException | IOException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());


                sysLogger.debug("Test outcome {} error count {}", getTestResult().getOutcome(),
                        getTestResult()
                                .getErrors().size());
                if (!IsTestFailed()) {
                    PassTest();
                }


            }

        }




        /**
         *
         * @param igPattern valid filename regex
         * @param imageFileName filename to test
         * @return   truth value of the file name matching the pattern completely - once only, and containing the
         * full expression
         */
        private  boolean ImagePassesTest( Pattern igPattern, String imageFileName) {
                Matcher matcher = igPattern.matcher(imageFileName);
                return matcher.matches() && matcher.start() == 0;
        }


        @Override
        public String getName() {
            return getTestName();
        }
    }
}
