package io.bdrc.am.audit;

import io.bdrc.am.audit.audittests.ImageSizeTests;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
           assertFalse("Test passed, expected fail.",tr.Passed());
           assertEquals("Should have failed on exception",2L,(long)tr.getOutcome());
       }

       @Test
        public void TestNoDir() {
        Hashtable<String,String> _testParams = new Hashtable<String,String>() {{
            // This value is for published images
            // put("DerivedImageGroupParent",  "images");
            // this tests our collateral
            put("DerivedImageGroupParent",  "testImages");
            put("MaximumImageSize","42");
        }};

           ImageSizeTests imageSizeTests = runTest("/IDontExist", _testParams);
           TestResult tr =  imageSizeTests.getTestResult();
           assertFalse("Test failed when should pass",tr.Passed());
           assertEquals("Should have failed on FileNotFound",2L,(long)tr.getOutcome());
       }

    @Test
    public void TestTooBigfail() {
        Hashtable<String,String> _testParams = new Hashtable<String,String>() {{
            // This value is for published images
            // put("DerivedImageGroupParent",  "images");
            // this tests our collateral
            put("DerivedImageGroupParent",  "testImages");
            put("MaximumImageSize","200k");
        }};
        ImageSizeTests imageSizeTests = runTest("src/test/images/WCalibrate", _testParams);
        TestResult tr =  imageSizeTests.getTestResult();
        assertFalse("Passed when should have failed",tr.Passed());
        assertEquals(2L,(long)tr.getOutcome());
    }


    @Test
    public void TestNoProperty() {
        Hashtable<String,String> _testParams = new Hashtable<String,String>() {{
            // This value is for published images
            // put("DerivedImageGroupParent",  "images");
            // this tests our collateral
            put("DerivedImageGroupParent",  "testImages");
          //  put("MaximumImageSize","nonIntegerShouldFail");
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
