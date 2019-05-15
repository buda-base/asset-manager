package io.bdrc.am.audit;



import io.bdrc.am.audit.audittests.ImageAttributeTests;

import io.bdrc.am.audit.iaudit.Outcome;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestProcessedImage extends AuditTestTestBase {


    @Rule
    public final TemporaryFolder rootFolder = new TemporaryFolder();

    /**
     * Tests we detect a file which passes its test
     */
    @Test
    public void TestImagePasses() {
        TestResult tr = runSizeTest("400000");
        Assert.assertTrue("Test failed, expected pass", tr.Passed());
    }

    @Test
    public void TestImageFailsTooLarge() {
        TestResult tr = runSizeTest("400000");
        Assert.assertTrue("Test passed, expected fail", tr.getOutcome() == Outcome.FAIL);
    }

    /**
     * Test image which is an otherTiffFails
     */
    public void TestImageOtherFails() {}

    private TestResult runSizeTest(String sizeParam) {
        Hashtable<String,String> _activeSequenceTestParams = new Hashtable<String,String>() {{
            put("DerivedImageGroupParent", "testImage");
            put("MaximumImageSize",sizeParam);
        }};
        ImageAttributeTests imageAttributeTests = runTest("../../test/WPass", _activeSequenceTestParams);
        return imageAttributeTests.getTestResult();
    }


    private ImageAttributeTests runTest(String path,Hashtable<String,String> igParents ) {
        ImageAttributeTests st = new ImageAttributeTests(logger);
        st.setParams(path, igParents);
        st.LaunchTest();

        return st;
    }

}
