package io.bdrc.am.audit;


import io.bdrc.am.audit.audittests.*;
import io.bdrc.am.audit.iaudit.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import static org.junit.Assert.*;

public class TestFileSequence extends AuditTestTestBase{


    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();

    private FileSequenceBuilder _fileSequenceBuilder ;

    @Before
    public void CreateFileBuilder() {
        _fileSequenceBuilder = new FileSequenceBuilder(rootFolder);
    }

    @Test
    public void TestPassingFiles() throws IOException {
        File fileRoot = _fileSequenceBuilder.BuildPassingFiles();
        FileSequence fst = new FileSequence(fileRoot.getAbsolutePath());
        fst.LaunchTest();

        assertTrue("Test did not pass when it should",fst.IsTestPassed());
    }


    @Test
    public void TestMissingFiles() throws IOException {
        File fileRoot = _fileSequenceBuilder.BuildMissingFiles(2);
        FileSequence fst = new FileSequence(fileRoot.getAbsolutePath());
        fst.LaunchTest();

        assertTrue("Test did not pass when it should",fst.IsTestFailed());

        // Look at the actual errors. there should be 1, and it should have duplicates
        // 1 .. 12 in its error array
    }

    @Test
    public void TestDuplicateFiles() throws IOException {
        File fileRoot = _fileSequenceBuilder.BuildDuplicateFiles(2);
        FileSequence fst = new FileSequence(fileRoot.getAbsolutePath());
        fst.LaunchTest();

        assertTrue("Test passed",fst.IsTestFailed());
        TestResult tr = fst.getTestResult();
        ArrayList<TestMessage> errors = tr.getErrors();

        // we created four directories, each with 12 duplicate files
        assertEquals(48, errors.size());
        assertEquals(Outcome.DUP_SEQUENCE, errors.get(0).getOutcome())  ;

        String errorText = errors.get(0).getMessage() ;
        assertFalse("Should have a message",isEmpty(errorText));
    }

    @Test
    public void TestNotExist() {
        FileSequence st = new FileSequence("/MrMxyzptlk");
        st.LaunchTest();

        assertTrue(st.IsTestFailed());
        TestResult tr = st.getTestResult();
        ArrayList<TestMessage> errors = tr.getErrors();

        assertEquals(1, errors.size());
        assertEquals(Outcome.ROOT_NOT_FOUND, errors.get(0).getOutcome())  ;
    }


    @Test
    public void setPath() {
        final String whanThatAprille = "WhanThatAprille";
        FileSequence st = new FileSequence(whanThatAprille);
        assertEquals(st.getPath(),whanThatAprille);
    }
}


