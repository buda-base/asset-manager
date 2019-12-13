package io.bdrc.am.audit;


import io.bdrc.am.audit.audittests.*;
import io.bdrc.am.audit.iaudit.*;
import io.bdrc.am.audit.iaudit.message.TestMessage;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import static org.junit.Assert.*;

public class TestFileSequence extends AuditTestTestBase{


    @Rule
    public final TemporaryFolder rootFolder = new TemporaryFolder();

    // private FileSequenceBuilder _noIGPfileSequenceBuilder;
    private FileSequenceBuilder _IGPfileSequenceBuilder;

    private final Hashtable<String, String> _emptySequenceTestParams = new Hashtable<>();
    private final Hashtable<String,String> _activeSequenceTestParams = new Hashtable<String,String>() {{
            put("ArchiveImageGroupParent", "harkeBeepar0lYn");
            put("DerivedImageGroupParent", "SchmengUndDreck");
        }};


    @Before
    public void CreateFileBuilders() {
       //  _noIGPfileSequenceBuilder = new FileSequenceBuilder(rootFolder);
        _IGPfileSequenceBuilder = new FileSequenceBuilder(rootFolder,_activeSequenceTestParams.values());
    }

    @Test
    public void TestPassingFiles() throws IOException {
        File fileRoot =  _IGPfileSequenceBuilder.BuildPassingFiles();
        FileSequence fst = runTest(fileRoot.getAbsolutePath(),_activeSequenceTestParams);

        assertTrue("Test did not pass when it should",fst.IsTestPassed());
    }


    @Test
    /*
     * An image group folder which has subfolders. It should fail a different test,
     * but not fail the sequence test. Test that FileSequence disregards subfolders.
     */
    public void TestIGWithSubFolders() throws IOException {
        File fileRoot =  _IGPfileSequenceBuilder.BuildFileSequencePassingFiles();
        FileSequence fst = runTest(fileRoot.getAbsolutePath(),_activeSequenceTestParams);

        assertTrue("Test did not pass when it should",fst.IsTestPassed());
    }

    @Test
    public void TestMissingFiles() throws IOException {
        File fileRoot = _IGPfileSequenceBuilder.BuildMissingFiles(12,2);
        FileSequence fst = runTest(fileRoot.getAbsolutePath(),_activeSequenceTestParams);
        assertTrue("Test did not fail when it should have.",fst.IsTestFailed());

    }

    @Test
    @Ignore
    public void TestSomething()  {
         Hashtable<String,String> _activeSequenceTestParams = new Hashtable<String,String>() {{
            put("ArchiveImageGroupParent", "archive");
            put("DerivedImageGroupParent", "image");
        }};
        FileSequence fst = runTest("/Users/jimk/tmp/AuditToolTestData/W1KG11900", _activeSequenceTestParams);
        TestResult tr = fst.getTestResult();
        Assert.assertFalse("Test should have failed", tr.Passed());

    }

    @Test
    public void TestDuplicateFiles() throws IOException {
        File fileRoot = _IGPfileSequenceBuilder.BuildMissingFiles(12,1,2);
        FileSequence fst = runTest(fileRoot.getAbsolutePath(),_activeSequenceTestParams);

        assertTrue("Test passed",fst.IsTestFailed());
        TestResult tr = fst.getTestResult();
        ArrayList<TestMessage> errors = tr.getErrors();

        // We should have this many errors:
        // One for each folder created, which is _activeSequenceTestParams * _IGPfileSequenceBuilder
        // .getImageGroupsPerParent()
        // One for each duplicate file in each folder, which should be 12 (the first Fill parameter), plus one for each
        // folder which contains the errors ( 12 + 1 = 13)
        int nExpected = ( _activeSequenceTestParams.size() )* _IGPfileSequenceBuilder.imageGroupsPerParent() * 13   ;

        Assert.assertEquals(nExpected, errors.size());

        Assert.assertEquals (LibOutcome.DIR_FAILS_SEQUENCE, errors.get(0).getOutcome()) ;

        String errorText = errors.get(0).getMessage() ;
        assertFalse("Should have a message",isEmpty(errorText));
    }

    @Test
    public void TestNotExist() {

        FileSequence st = runTest("/MrMxyzptlk",_activeSequenceTestParams);
        assertTrue(st.IsTestFailed());
        TestResult tr = st.getTestResult();
        ArrayList<TestMessage> errors = tr.getErrors();

        assertEquals(1, errors.size());
        assertEquals(LibOutcome.ROOT_NOT_FOUND, errors.get(0).getOutcome())  ;
    }

    @Test
    public void TestFilterOutFiles() {
        Hashtable<String,String> _activeSequenceTestParams = new Hashtable<String,String>() {{
            put("ArchiveImageGroupParent", "testImages");
            put("IgnoreFileExpressions","*.json,hoopsty");
        }};
        FileSequence st = runTest("src/test/images/WFilterOutJson",_activeSequenceTestParams);
        assertTrue(st.IsTestPassed());
    }


    @Test
    public void setPath() {
        final String whanThatAprille = "WhanThatAprille";
        FileSequence st = new FileSequence(logger);
        st.setParams(whanThatAprille,_emptySequenceTestParams);
        assertEquals(st.getPath(),whanThatAprille);
    }

    private FileSequence runTest(String path,Hashtable<String,String> igParents ) {
        FileSequence st = new FileSequence(logger);
        st.setParams(path, igParents);
        st.LaunchTest();

        return st;
    }
}


