package io.bdrc.am.audit;



import io.bdrc.am.audit.audittests.ImageAttributeTests;
import io.bdrc.am.audit.audittests.TestArgNames;
import io.bdrc.am.audit.iaudit.TestResult;
import io.bdrc.am.audit.iaudit.message.TestMessage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.List;

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

    private static boolean validOutcome(TestMessage e) {
        return e.getOutcome() == 110 || e.getOutcome() == 115 ;
    }

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

    @Test public void NoEXIFPasses()  {
        TestResult tr = runAttributesTest("src/test/images/EXIF/pass");
        Assert.assertFalse( "Test passed, expected fail", tr.Passed());
    }

    @Test public void MixedRotationandNoTagsFails() {

        TestResult tr = runAttributesTest("src/test/images/EXIF/makePass");
        Assert.assertTrue( "Test failed, expected pass", tr.Failed());

        // Only one file should have failed. The others should have passed
        List<TestMessage> errors = tr.getErrors();

        Assert.assertEquals("Only one file should have failed",1, errors.size());

        Assert.assertTrue("I8LS738230116.jpg_original should have failed",
                errors.get(0).getMessage().contains("I8LS738230116.jpg_original"));
    }

    @Test
    public void CorruptFileTest() {
        TestResult tr = runAttributesTest("src/test/images/Corrupt/corrupt");

        Assert.assertFalse("Corrupt images must not pass",tr.Passed());

        // Get file count of Corrupt/corrupt/testImages/imagegroup1
        File testDir = new File("src/test/images/Corrupt/corrupt/testImages/imagegroup1");

        // Each file should throw two errors - fix later
        int expectedNumFiles = 2 * testDir.listFiles().length;
        int actualNumErrors = tr.getErrors().size();
        Assert.assertEquals("incorrect number of files reported errors",expectedNumFiles,actualNumErrors);

        for (final TestMessage error : tr.getErrors()) {
            System.out.println(MessageFormat.format("code: {0} text {1}", error.getOutcome(), error.getMessage()));
        }

        // The error code we're looking for is 110  (no suitable reader for file)
        // or 115 ( EXIF format)

        Assert.assertTrue(tr.getErrors().stream().allMatch(e -> { return e.getOutcome() == 110 || e.getOutcome() == 115 ;}));

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
