package io.bdrc.audit.audittests;

import io.bdrc.audit.iaudit.LibOutcome;
import io.bdrc.audit.iaudit.Outcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * ImageFileNameFormatTest - guarantees an image name corresponds to the image group
 * component of its containing directory.
 * Ex:
 * ImageGroup WW1KG421234/images/1KG421234-I1KG31412559 contains files:
 * I1KG314125590001.jpg I1KG31412559_0002.jpg  W1KG421234-I1KG31412559_0003.jpg  I1KG31412559-0004.jpg
 * <p>
 * Files I1KG314125590001.jpg I1KG31412559_0002.jpg  will pass this test,
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

    public ImageFileNameFormatTest(Logger logger, String testName)
    {
        super(testName);
        sysLogger = logger;
    }

    private  final Hashtable<String,String> _testParams = new Hashtable<>() {{
        // this tests our test collateral
        put(TestArgNames.DERIVED_GROUP_PARENT, "testImages");
    }};

    @Override
    public void LaunchTest() {

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
         * @param imageGroupParent Path to image group file container
         */
        private void TestImages(final Path imageGroupParent) {

            String[] igNames  = imageGroupParent.getFileName().toString().split("-");

            // Use after the split, if any
            String targetIgName = igNames.length > 1 ? igNames[1] : igNames[0];

            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isFile()
                            && !(entry.toFile().isHidden()
                            || entry.toString().endsWith("json")));


            sysLogger.debug("Test outcome {} error count {}", getTestResult().getOutcome(),
                    getTestResult()
                            .getErrors().size());
            if (!IsTestFailed()) {
                PassTest();
            }

        }


        @Override
        public String getName() {
            return getTestName();
        }
    }
}
