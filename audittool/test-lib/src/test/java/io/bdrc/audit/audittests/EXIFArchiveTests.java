package io.bdrc.audit.audittests;

import io.bdrc.audit.iaudit.LibOutcome;
import io.bdrc.audit.iaudit.TestResult;
import io.bdrc.audit.iaudit.message.TestMessage;
import org.junit.Assert;
import org.junit.Test;


import java.util.Hashtable;
import java.util.List;

public class EXIFArchiveTests extends AuditTestTestBase {
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
        //         put(TestArgNames.DERIVED_GROUP_PARENT,"images");
        // this tests our test collateral
        put(TestArgNames.ARC_GROUP_PARENT,  "testImages");
        put(TestArgNames.DERIVED_GROUP_PARENT,  "testImages");
    }};

    @Test
    public void ArchiveAndImageSameTestDifferentOutcome() {
        MixedRotationAndNoTagsFails(TestDictionary.EXIF_ARCHIVE_TEST_NAME, LibOutcome.INVALID_ARCHIVE_EXIF);
        MixedRotationAndNoTagsFails(TestDictionary.EXIF_IMAGE_TEST_NAME, LibOutcome.INVALID_IMAGE_EXIF);
    }

    /**
     * This tests that variants only emit error codes they are supposed to
     */
    @Test(expected=java.lang.AssertionError.class)
    public void ArchiveEXIFTestDoesntThrowImageEXIFError() {
        MixedRotationAndNoTagsFails(TestDictionary.EXIF_ARCHIVE_TEST_NAME, LibOutcome.INVALID_IMAGE_EXIF);
    }

    @Test(expected=java.lang.AssertionError.class)
    public void ImageEXIFTestDoesntThrowArchiveEXIFError() {
        MixedRotationAndNoTagsFails(TestDictionary.EXIF_IMAGE_TEST_NAME, LibOutcome.INVALID_ARCHIVE_EXIF);
    }

    @Test
    public void ImageDirectoryTest() {
        runEXIFTest("src/test/images/EXIF.Bad_dirs",TestDictionary.EXIF_IMAGE_TEST_NAME);

    }
    @Test
    public void WhatsInThisTest() {
        runEXIFTest("src/test/images/EXIF/makePass",TestDictionary.EXIF_IMAGE_TEST_NAME);

    }

    public void MixedRotationAndNoTagsFails(String testName, Integer expectedOutcome) {

        TestResult tr = runEXIFTest("src/test/images/EXIF/makePass",testName);
        Assert.assertTrue( "Test passed, expected fail", tr.Failed());

        // Only one file should have failed. The others should have passed
        List<TestMessage> errors = tr.getErrors();

        Assert.assertEquals("Only one file should have failed",1, errors.size());

        Assert.assertTrue("I8LS738230116.jpg_original should have failed",
                errors.get(0).getMessage().contains("I8LS738230116.jpg_original"));
        Assert.assertEquals(expectedOutcome,errors.get(0).getOutcome());
    }


    private TestResult runEXIFTest(String grandParentOfImageGroup, final String testName)  {
        EXIFTest exifArchiveTest = runTest(grandParentOfImageGroup, testName, _testParams);
        return exifArchiveTest.getTestResult();
    }

    private EXIFTest runTest(String path, final String testName, Hashtable<String,String> testParams ) {
        EXIFTest st = new EXIFTest(logger, testName);
        st.setParams(path, testParams);
        st.LaunchTest();

        return st;
    }
}
