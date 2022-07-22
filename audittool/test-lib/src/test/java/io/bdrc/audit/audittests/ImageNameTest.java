package io.bdrc.audit.audittests;

import io.bdrc.audit.iaudit.TestResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Hashtable;

public class ImageNameTest extends AuditTestTestBase {

    // host a group of temporary work folders
    @Rule
    public final TemporaryFolder workRoot = new TemporaryFolder();

    private FileNameBuilder fnb ;

    private  final static Hashtable<String,String> _testParams = new Hashtable<>() {{
        put(TestArgNames.DERIVED_GROUP_PARENT, "testImages");
    }};

    @Before
    public void MakeTestFolders() throws IOException
    {
        fnb = new FileNameBuilder(workRoot,
                _testParams.get(TestArgNames.DERIVED_GROUP_PARENT));
    }

    @Test
    public void SingleHyphenSucceeds() throws IOException {
        fnb.BuildPassesOneSuffix();
        TestResult tr = RunTest(workRoot.getRoot().getAbsolutePath());
        Assert.assertTrue(tr.Passed());
    }

    @Test
    public void DoubleHyphenSucceeds() throws IOException {
        fnb.BuildPassesTwoSuffix();
        TestResult tr = RunTest(workRoot.getRoot().getAbsolutePath());
        Assert.assertTrue(tr.Passed());
    }

    @Test
    public void MalformedFails() throws IOException {
        fnb.BuildFails();
        TestResult tr = RunTest(workRoot.getRoot().getAbsolutePath());
        Assert.assertTrue(tr.Failed());
    }



    private TestResult RunTest(String workRoot) {
        ImageFileNameFormatTest ifft = new ImageFileNameFormatTest(logger, "ImageFileFormatTest");
        ifft.setParams(workRoot, ImageNameTest._testParams);
        ifft.LaunchTest();
        return ifft.getTestResult();
    }

}

