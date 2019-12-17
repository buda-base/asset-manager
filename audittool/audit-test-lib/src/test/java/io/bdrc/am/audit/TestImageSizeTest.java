package io.bdrc.am.audit;

import io.bdrc.am.audit.audittests.ImageSizeTests;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Hashtable;

public class TestImageSizeTest extends AuditTestTestBase {


        @Rule
        public final TemporaryFolder rootFolder = new TemporaryFolder();



    @Test
        public void TestNoSize() {
        Hashtable<String,String> _testParams = new Hashtable<String,String>() {{
            // This value is for published images
            // put("DerivedImageGroupParent",  "images");
            // this tests our collateral
            put("DerivedImageGroupParent",  "testImages");
            put("MaximumImageSize","nonIntegerShouldFail");
        }};
           ImageSizeTests imageSizeTests = runTest("src/test/images/WCalibrate", _testParams);
           TestResult tr =  imageSizeTests.getTestResult();
       }


    private ImageSizeTests runTest(String path, Hashtable<String,String> testParams ) {
        ImageSizeTests st = new ImageSizeTests(logger);
        st.setParams(path, testParams);
        st.LaunchTest();

        return st;
    }

}
