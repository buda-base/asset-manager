package io.bdrc.audit.audittests;

import io.bdrc.audit.iaudit.LibOutcome;
import io.bdrc.audit.iaudit.Outcome;
import io.bdrc.audit.iaudit.PropertyManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ImageFileNameFormatTest - guarantees an image name corresponds to the image group
 * component of its containing directory.
 * Ex:
 * ImageGroup WW1KG421234/images/1KG421234-I1KG31412559 contains files:
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
 *
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
            String[] igNames = imageGroup.getFileName().toString().split("-");

            // Use after the split, if any
            String targetIgName = igNames[igNames.length - 1];

            // Create a regex for the image group name


            // filter in files that are not hidden and not json and not passing the test
            DirectoryStream.Filter<Path> invalidImageFilter =
                    entry -> (entry.toFile().isFile()
                            && !(entry.toFile().isHidden()
                            || entry.toString().endsWith("json"))
                            && !ImagePassesTest(entry.getFileName().toString(), targetIgName)

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
         * ValidImageFileName
         * @param imageFileName image file name (no path) to test
         * @param targetIgName pattern that image file name must match
         * @return truth value of:
         * - imageFileName starts with targetIgName
         * - the rest of the filename, without the 'targetIgName' must be digits (the same
         *   as the FileSequence.Sequence length).
         *   TBD if the suffix has to be limited, let's not for now
         */
        private  boolean ImagePassesTest( String imageFileName, final String targetIgName) {

            // TODO: Adopt this

            // TODO:  Put the pattern compiler up in the caller - don't compile for each file
                Pattern smurf;

                //TODO: Case insensitivity

                // 12345[-_]{0,1}\d{4}\..*
                // 12345123456789.4  matches at index 5 - must inist match at 0
                smurf = Pattern.compile(String.format("%s[-_]{0,1}\\d{4}\\..*",args[0]));

                Matcher matcher = smurf.matcher(args[1]);

                // Just pass fail on matches and 0
                String resultm = "does not match";
                if (matcher.matches() && matcher.start() == 0) {
                    resultm = "matches";
                }
                System.out.printf("The regex pattern %s [[ %s the entire string %s ]  and [ starts at index 0. ]] \\n", args[0],
                        resultm, args[1] );
            }
            boolean isOkFileName = imageFileName.startsWith(targetIgName);
            String restOfFileName = FilenameUtils.getBaseName(StringUtils.remove(imageFileName,targetIgName));
            boolean isOKSequenceLength = (restOfFileName.length() == _platformSequenceLength);
            boolean isOkInteger;
            try {
                Integer.parseInt(restOfFileName);
                isOkInteger = true;
            } catch (NumberFormatException e) {
                isOkInteger = false;
            }

            return isOkFileName && isOKSequenceLength && isOkInteger ;
        }


        @Override
        public String getName() {
            return getTestName();
        }
    }
}
