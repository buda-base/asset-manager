package io.bdrc.am.audit;

import io.bdrc.am.audit.audittests.ImageSizeTests;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestImageSizeTest extends AuditTestTestBase {


        @Rule
        public final TemporaryFolder rootFolder = new TemporaryFolder();

       @Test
        public void TestNoSize() {
           String[] imageTestParams = {
                   "DerivedImageGroupParent=testImages",
                   String.format("MaximumImageSize=%s","HowdyFolks, I'm set up to fail.")
           };

           ImageSizeTests imageSizeTests = runTest("src/test/images/WCalibrate", imageTestParams);
           TestResult tr =  imageSizeTests.getTestResult();
       }


    private ImageSizeTests runTest(String path, String[] testParams ) {
        ImageSizeTests st = new ImageSizeTests(logger);
        st.setParams(path, testParams);
        st.LaunchTest();

        return st;
    }

}
