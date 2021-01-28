package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.LibOutcome;
import io.bdrc.am.audit.iaudit.Outcome;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.bdrc.am.audit.audittests.TestArgNames.*;

public class ImageSizeTests extends PathTestBase {



    /**
     * Constructor with builtin logger
     * Useful if you want your slf4j profile to drive logging.
     * note base class must pass its test (such as directory exists)
     * IDC about https://stackoverflow.com/questions/285177/how-do-i-call-one-constructor-from-another-in-java
     * The factory method is not the simplest way to write an external library.
     */
    public ImageSizeTests() {
        this(LoggerFactory.getLogger(ImageSizeTests.class));
    }
    /**
     * new AuditTestBase
     *
     * @param logger internal test only logger
     */
    public ImageSizeTests(Logger logger) {
        super("ImageSizeTest");
        sysLogger = logger;
    }

    // see parseFileSize()
    // This one fails 123456789. and 324, because it wants the last character to fill the
    // second group
    //private final  String  NUMERIC_PATTERN  = "([\\d.,]+)\\s*(\\w)";
    // So make the second group optional
    // But this fails a55, because it doesnt require the string begin with numbers
//    private final  String  NUMERIC_PATTERN  = "([\\d.,]+)\\s*(\\w?)";
    private final  String  NUMERIC_PATTERN  = "^([\\d.,]+)\\s*(\\w?)$";

    public class ImageSizeTestOperation implements AuditTestBase.ITestOperation {
        @Override
        public void run() throws IOException {

            Long imageLimit = parseFilesize(keywordArgParams.getOrDefault(MAX_IMAGE_FILE_SIZE,"400K"));

            if (IsTestFailed())
            {
                return;
            }
            // This test only examines derived image image groups
            Path examineDir = Paths.get(getPath(), keywordArgParams.getOrDefault(DERIVED_GROUP_PARENT,""));

            // Creating the filter for non-hidden directories
            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isDirectory() && !(entry.toFile().isHidden()));

            try (DirectoryStream<Path> imageGroupDirs = Files.newDirectoryStream(examineDir, filter)) {
                for (Path imagegroup : imageGroupDirs) {
                    TestImages(imagegroup, imageLimit);
                }
                if (!IsTestFailed())
                {
                    PassTest();
                }
            } catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC,die.getMessage());
                throw die;
            }
            catch (NoSuchFileException nsfe)
            {
                String badPath = nsfe.getFile();
                sysLogger.error("No such file {}", badPath);
                FailTest(LibOutcome.ROOT_NOT_FOUND, badPath);
            }


        }

        private void TestImages(final Path imageGroup, Long imageLimit) {

            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isFile() && !(entry.toFile().isHidden()));

            try (DirectoryStream<Path> imageFiles = Files.newDirectoryStream(imageGroup, filter)) {
                for (Path imageFile : imageFiles) {
                    File fileObject = imageFile.toAbsolutePath().toFile();
                    Long imageLength = fileObject.length();
                    if (imageLength > imageLimit) {
                        FailTest(LibOutcome.FILE_SIZE,fileObject.toString(),
                                imageLength.toString(), imageLimit.toString());
                    }

                }
            } catch (Exception eek) {
                FailTest(Outcome.SYS_EXC, "ImageAttributeTest", " in " + imageGroup.toString() + ":" + eek
                        .getMessage
                                ());
            }
        }

        @Override
        public String getName() {
            return getTestName();
        }


        /**
         * parse human readable file sizes
         *
         * @param in input string, in format numeric, or numeric[KMG][ ]*B{0,1}
         * @return value in readable character, optionally suffixed with KGM, etc.
         */
         long parseFilesize(String in) {
            in = in.trim().replaceAll(",", "").toUpperCase();
            // One or more digits, followed by an optional decimal point, then more digits
             // optionally followed by some whitespace, and then optionally by one of the
             // characters K,M,G for scale

            final Matcher m = Pattern.compile(NUMERIC_PATTERN).matcher(in);

            if (!m.find())
            {
                FailTest(LibOutcome.BAD_FILE_SIZE_ARG,"in");
                return -1;
            }


            int scale = 1;
             String scaleStr ;
             try
             {
                 scaleStr = m.group(2);
                 if (!StringUtils.isEmpty(scaleStr))
                 {

                     // clever: uses fall through
                     switch (m.group(2).charAt(0))
                     {
                         case 'G':
                             scale *= 1024;
                         case 'M':
                             scale *= 1024;
                         case 'K':
                             scale *= 1024;
                             break;
                         default:
                             FailTest(LibOutcome.BAD_FILE_SIZE_ARG, "in");
                             break;
                     }
                 }
             } catch (IndexOutOfBoundsException ie)
             {
                 // do nothing
             }

             Double maxSize = -1.0 ;
             try {
                 maxSize = Double.parseDouble(m.group(1));
             }
             catch(NumberFormatException nfe)
             {
                 FailTest(LibOutcome.BAD_FILE_SIZE_ARG,m.group(1));
             }

            return (IsTestFailed()) ? -1 : Math.round( maxSize * scale);

        }
    }

    @Override
    public void LaunchTest() {
        TestWrapper(new ImageSizeTests.ImageSizeTestOperation());
    }

    /**
     * Set all test parameters (not logging or framework)
     * TestProcessedImage expects
     * 1. path - parent container
     * 2. kwargs: These named arguments are required in (String [])(params[1])
     * "DerivedImageGroupParent=folderName
     * "ImageFileSizeLimit=0....n"
     *
     * @param params implementation dependent optional parameters
     */
    @Override
    public void setParams(final Object... params) {
        if ((params == null) || (params.length < 2)) {
            throw new IllegalArgumentException(String.format("Audit test :%s: Required Arguments path, and " +
                            "propertyDictionary not given.",
                    getTestName()));
        }
        super.setParams(params);
    }
}
