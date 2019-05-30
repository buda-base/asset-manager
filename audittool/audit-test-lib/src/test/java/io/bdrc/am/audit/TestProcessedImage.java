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
    public void TestImagePasses() throws IOException {
        TestResult tr = runSizeTest("400000");
        Assert.assertTrue("Test failed, expected pass", tr.Passed());
    }

    @Test
    public void TestImageFailsTooLarge() throws IOException {
        TestResult tr = runSizeTest("400000");
        Assert.assertTrue("Test passed, expected fail", tr.getOutcome() == Outcome.FAIL);
    }

    /**
     * Test image which is an otherTiffFails
     */
    public void TestImageOtherFails() {}

    private TestResult runSizeTest(String sizeParam) throws IOException {
        String[] imageTestParams = {
                "DerivedImageGroupParent=testImages",
                String.format("MaximumImageSize=%s",sizeParam)
        };

        /*
         *  W1KG13805	I1KG15773	49420820	3240	5083	RGB	TIFF		raw	toolarge-tiffnotgroup4-nonbinarytif
            W1KG13806	I1KG15775	   55550	1664	2560	1	TIFF		tiff_lzw	tiffnotgroup4
            W1KG13823	I1KG16108	  324767	3764	 689	1	TIFF		raw	tiffnotgroup4
            W1KG13823	I1KG16108	 1590833	2771	 574	L	TIFF		raw	toolarge-tiffnotgroup4-nonbinarytif
         */
//        ImageAttributeTests imageAttributeTests = runTest("src/test/images/WOtherTiffFails", imageTestParams);
        ImageAttributeTests imageAttributeTests = runTest("src/test/images/WCalibrate", imageTestParams);
        return imageAttributeTests.getTestResult();
    }


    private ImageAttributeTests runTest(String path, String[] testParams ) {
        ImageAttributeTests st = new ImageAttributeTests(logger);
        st.setParams(path, testParams);
        st.LaunchTest();

        return st;
    }

}
