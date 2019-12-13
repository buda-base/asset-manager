package io.bdrc.am.audit;

import io.bdrc.am.audit.audittests.ImageAttributeTests;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.Hashtable;

public class TestFilteredErrors extends AuditTestTestBase {

    private  final Hashtable<String,String> _testParams = new Hashtable<String,String>() {{
        // This value is for published images
        // put("DerivedImageGroupParent",  "images");
        // this tests our collateral
        put("DerivedImageGroupParent",  "testImages");
    }};


    @Test
    public void TestFilteredErrorFail()
    {

        TestResult tr = runAttributesTest("src/test/images/WOtherTiffFails");
        Assert.assertFalse( "Test passed, expected fail", tr.Passed());

        // Magic property: see shell.properties
        _testParams.put("ErrorsAsWarning","110,111,106");
        tr = runAttributesTest("src/test/images/WOtherTiffFails");
        Assert.assertTrue("Test failed, expected pass", tr.Passed());
    }

    private TestResult runAttributesTest(String grandParentOfImageGroup)  {
        ImageAttributeTests imageAttributeTests = runTest(grandParentOfImageGroup, _testParams);
        return imageAttributeTests.getTestResult();
    }

    private ImageAttributeTests runTest(String path, Hashtable<String,String> testParams ) {
        ImageAttributeTests st = new ImageAttributeTests(logger);

        // Hmm TODO: Needs to consume properties differently
        st.setParams(path, testParams);
        st.LaunchTest();

        return st;
    }

}
