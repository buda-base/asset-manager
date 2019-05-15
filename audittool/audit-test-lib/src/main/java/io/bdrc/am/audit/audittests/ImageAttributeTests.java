package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.Outcome;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.*;

public class ImageAttributeTests extends PathTestBase {
    /**
     * new AuditTestBase
     *
     * @param logger internal logger
     */
    public ImageAttributeTests(Logger logger)
    {
        super("ImageAttributeTests");
        sysLogger = logger;
    }

    public class ImageAttributes implements AuditTestBase.ITestOperation {

        @Override
        public void run() throws IOException {
            // throw new IOException("Not implemented");
            Path rootFolder = Paths.get(getPath());

            // This test only examines derived image image groups
            Path examineDir = Paths.get(getPath(), testParameters.get("DerivedImageGroupParent"));

// Creating the filter
            DirectoryStream.Filter<Path> filter =
                    entry -> (entry.toFile().isDirectory() && !(entry.toFile().isHidden()));

            try (DirectoryStream<Path> imageGroupDirs = Files.newDirectoryStream(examineDir, filter)) {
                for (Path imagegroup : imageGroupDirs) {
                    TestImages(imagegroup);
                }

            } catch (DirectoryIteratorException die) {
                sysLogger.error("Directory iteration error", die);
                FailTest(Outcome.SYS_EXC, die.getCause().getLocalizedMessage());

            }
        }

        @Override
        public String getName() {
            return getTestName();
        }

    private void TestImages(final Path imagegroup) {


    }

}

    @Override
    public void LaunchTest() {
        TestWrapper(new ImageAttributes());
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
        super.setParams(params[0]);
        LoadParameters((String[]) (params[1]));
    }
}
