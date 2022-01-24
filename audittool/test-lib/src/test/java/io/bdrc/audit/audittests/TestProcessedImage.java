package io.bdrc.audit.audittests;


import io.bdrc.audit.iaudit.TestResult;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Objects;

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


    @Test
    public void CorruptFileTest() {
        String rootDir = "src/test/images/Corrupt/corrupt";
        TestResult tr = runAttributesTest(rootDir);

        Assert.assertFalse("Corrupt images must not pass",tr.Passed());

        // Get file count of Corrupt/corrupt/testImages/imagegroup1
        File testDir = Paths.get(rootDir , "testImages/imagegroup1").toFile();

        // Each file should throw two errors - fix later
        int expectedNumFiles = Objects.requireNonNull(testDir.listFiles()).length;
        int actualNumErrors = tr.getErrors().size();
        Assert.assertEquals("incorrect number of files reported errors",expectedNumFiles,actualNumErrors);

//        for (final TestMessage error : tr.getErrors()) {
//            System.out.println(MessageFormat.format("code: {0} text {1}", error.getOutcome(), error.getMessage()));
//        }
        Assert.assertTrue(tr.getErrors().stream().allMatch(e -> e.getOutcome() == 110));
    }

    private TestResult runAttributesTest(String grandParentOfImageGroup)  {
        ImageAttributeTests imageAttributeTests = runTest(grandParentOfImageGroup, _testParams);
        return imageAttributeTests.getTestResult();
    }

    private ImageAttributeTests runTest(String path, Hashtable<String,String> testParams ) {
        ImageAttributeTests st = new ImageAttributeTests(logger,"ImageAttributeTests");
        st.setParams(path, testParams);
        st.LaunchTest();

        return st;
    }


}
