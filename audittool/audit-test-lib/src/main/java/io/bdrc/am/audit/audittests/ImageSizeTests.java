package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.Outcome;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageSizeTests extends PathTestBase {


    /**
     * new AuditTestBase
     *
     * @param logger internal test only logger
     */
    public ImageSizeTests(Logger logger) {
        super("ImageSizeTest");
        sysLogger = logger;
    }

    public class ImageSizeTestOperation implements AuditTestBase.ITestOperation {
        @Override
        public void run() throws IOException {

            Path rootFolder = Paths.get(getPath());

            // This test only examines derived image image groups
            Path examineDir = Paths.get(getPath(), keywordArgParams.getOrDefault("DerivedImageGroupParent",""));

            // Creating the filter for non-hidden directories
            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isDirectory() && !(entry.toFile().isHidden()));

            try (DirectoryStream<Path> imageGroupDirs = Files.newDirectoryStream(examineDir, filter)) {
                for (Path imagegroup : imageGroupDirs) {
                    TestImages(imagegroup);
                }
            } catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                throw die;
            }
            catch (NoSuchFileException nsfie)
            {
                sysLogger.error("No such file {}", getPath());
                FailTest(LibOutcome.ROOT_NOT_FOUND, getPath());
            }
        }

        private void TestImages(final Path imageGroup) {

            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isFile() && !(entry.toFile().isHidden()));

            Long imageLimit = parseFilesize(keywordArgParams.getOrDefault("MaximumImageSize","400K"));
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

            final Matcher m = Pattern.compile("([\\d]+.?[\\d]*)\\s*([KMG])?").matcher(in);

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
            return (IsTestFailed() ? -1 : Math.round(Double.parseDouble(m.group(1)) * scale);

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
