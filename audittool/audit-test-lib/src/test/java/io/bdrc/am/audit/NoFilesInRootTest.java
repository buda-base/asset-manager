package io.bdrc.am.audit;

import io.bdrc.am.audit.audittests.NoFilesInRoot;

import io.bdrc.am.audit.iaudit.Outcome;
import io.bdrc.am.audit.iaudit.TestMessage;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class NoFilesInRootTest extends AuditTestTestBase {



    @After
    public void tearDown()
    {
    }

    @Test
    public void TestNotExistFails() {
        String rootPath = "/MrMxyzptlk";
        NoFilesInRoot st = new NoFilesInRoot(logger);
        st.setParams(rootPath);
        st.LaunchTest();
        logger.debug("Calling {} for {}","TestNotExistFails", rootPath );
        assertTrue(st.IsTestFailed());
        TestResult tr = st.getTestResult();
        ArrayList<TestMessage> errors = tr.getErrors();

        assertEquals(1, errors.size());
        assertEquals(Outcome.ROOT_NOT_FOUND, errors.get(0).getOutcome())  ;
    }

    @Test
    public void NonExDirWithContentFails() {
        // TODO: Synthetic file stream
        String dirname = "/Users/jimk/tmp";

        logger.debug("Testing {} on {}", "NonExDirWithContentFails", dirname);
        NoFilesInRoot st = new NoFilesInRoot(logger);
        st.setParams(dirname);
        st.LaunchTest();

        // Should fail
        assertTrue(st.IsTestFailed());

        // should be one error
        TestResult tr = st.getTestResult();
        ArrayList<TestMessage> errors = tr.getErrors();
        assertEquals(1, errors.size());
        TestMessage tm = errors.get(0);
        assertEquals(Outcome.FILES_IN_MAIN_FOLDER,tm.getOutcome());

    }

    @Test
    public void setPath() {
        final String whanThatAprille = "WhanThatAprille";
        logger.debug("Calling {} on {}", "setPath", whanThatAprille);
        NoFilesInRoot st = new NoFilesInRoot(logger);
        st.setParams(whanThatAprille);
        assertEquals(st.getPath(),whanThatAprille);
    }

    @Test
    public void getTestName() {
        NoFilesInRoot dontcare = new NoFilesInRoot(logger);
        dontcare.setParams("Dontcare");
        String dcTestName = dontcare.getTestName();
        assertEquals("NoFilesInRoot",dcTestName);

    }
}