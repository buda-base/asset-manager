package io.bdrc.am.audit;



import io.bdrc.am.audit.audittests.ImageAttributeTests;

import io.bdrc.am.audit.iaudit.Outcome;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestProcessedImage extends AuditTestTestBase {


    @Rule
    public final TemporaryFolder rootFolder = new TemporaryFolder();

    /**
     * Tests we detect a file which passes its test
     * "src/test/images/WOtherTiffFails",
     */
    @Test
    public void TestImagePasses() throws IOException {
        TestResult tr = runAttributesTest("src/test/images/WPass");
        Assert.assertTrue("Test failed, expected pass", tr.Passed());
    }

    @Test
    public void TestImagePassesK() throws IOException {
        TestResult tr = runAttributesTest("src/test/images/WPass");
        Assert.assertTrue("Test failed, expected pass", tr.Passed());
    }

    @Test
    public void TestImageFailsTooLarge() throws IOException {
        TestResult tr = runAttributesTest("src/test/images/WPass");
        Assert.assertFalse( "Test passed, expected fail", tr.Passed());
    }

    @Test
    public void TestImageOtherFails() throws IOException {
        TestResult tr = runAttributesTest("src/test/images/WOtherTiffFails");
        Assert.assertFalse( "Test passed, expected fail", tr.Passed());
    }

    private TestResult runAttributesTest(String grandParentOfImageGroup)  {
        String[] imageTestParams = {
                "DerivedImageGroupParent=testImages"
        };

        ImageAttributeTests imageAttributeTests = runTest(grandParentOfImageGroup, imageTestParams);
        return imageAttributeTests.getTestResult();
    }

    @Test
    public void TestPassingFiles() {
        String[] imageTestParams = {
                "DerivedImageGroupParent=testImages"};

        // Some sample output from volume-manifest-tool
        /*                                     size     width   hght    PIL mode    4type compression   errors
         *  W1KG13805	I1KG15773..0200	    49420820	3240	5083	RGB	        TIFF	raw         toolarge-tiffnotgroup4-nonbinarytif
            W1KG13806	I1KG15775	           55550	1664	2560	1	        TIFF	tiff_lzw	tiffnotgroup4
            W1KG13823	I1KG16108..00004	  324767	3764	 689	1	        TIFF	raw	        tiffnotgroup4
            W1KG13823	I1KG16108...0003	 1590833	2771	 574	L	        TIFF	raw
            toolarge-tiffnotgroup4-nonbinarytif
         */
        ImageAttributeTests imageAttributeTests = runTest("src/test/images/WPass", imageTestParams);

        assertEquals(Outcome.PASS, imageAttributeTests.getTestResult().getOutcome());
    }


    private ImageAttributeTests runTest(String path, String[] testParams ) {
        ImageAttributeTests st = new ImageAttributeTests(logger);
        st.setParams(path, testParams);
        st.LaunchTest();

        return st;
    }

}
