package io.bdrc.am.audit;



import io.bdrc.am.audit.audittests.ImageAttributeTests;
import io.bdrc.am.audit.audittests.TestArgNames;
import io.bdrc.am.audit.iaudit.TestResult;
import io.bdrc.am.audit.iaudit.message.TestMessage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Hashtable;

public class TestProcessedImage extends AuditTestTestBase {

    /**
     * Put image group parents we want to test in here.
     * Possible  choices for keys are:
     *         ARC_GROUP_PARENT
     *         DERIVED_GROUP_PARENT
     *  Since we dont want to test archive images, we dont add it here.
     *  We're declaring here that only folders with the name 'testImages' contain
     *  folders we want to test.
     */
    private  final Hashtable<String,String> _testParams = new Hashtable<String,String>() {{
        // This value is for published images
        // put(DERIVED_GROUP_PARENT,  "images");
        // this tests our collateral
        put(TestArgNames.DERIVED_GROUP_PARENT,  "testImages");
    }};

    @Rule
    public final TemporaryFolder rootFolder = new TemporaryFolder();

    /**
     * Tests we detect a file which passes its test
     * "src/test/images/WOtherTiffFails",
     */
    @Test
    public void TestImagePasses()   {

        // jimk: try root as target/test-classes
//        TestResult tr = runAttributesTest("src/test/images/WPass");
        try
        {
            String current = new java.io.File( "." ).getCanonicalPath();
            String currentDir = System.getProperty("user.dir");
            logger.debug(". {} wd {}", current, currentDir);
            System.out.println (String.format(". %s wd %s", current, currentDir));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        TestResult tr = runAttributesTest("src/test/images/WPass");
        // TestResult tr = runAttributesTest("/Volumes/Archive/W21725");
        for (TestMessage tm : tr.getErrors()) {
            System.out.println(tm.getMessage());
        }
        Assert.assertTrue("Test failed, expected pass", tr.Passed());
    }

    @Test
    public void TestImageOtherFails()  {
        TestResult tr = runAttributesTest("src/test/imagesWOtherTiffFails");
        Assert.assertFalse( "Test passed, expected fail", tr.Passed());
    }

    private TestResult runAttributesTest(String grandParentOfImageGroup)  {
        ImageAttributeTests imageAttributeTests = runTest(grandParentOfImageGroup, _testParams);
        return imageAttributeTests.getTestResult();
    }

    private ImageAttributeTests runTest(String path, Hashtable<String,String> testParams ) {
        ImageAttributeTests st = new ImageAttributeTests(logger);
        st.setParams(path, testParams);
        st.LaunchTest();

        return st;
    }

}
