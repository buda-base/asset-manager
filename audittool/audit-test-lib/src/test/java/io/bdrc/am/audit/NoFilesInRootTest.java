package io.bdrc.am.audit;

import io.bdrc.am.audit.iaudit.LibOutcome;
import io.bdrc.am.audit.audittests.NoFilesInRoot;
import io.bdrc.am.audit.iaudit.message.TestMessage;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NoFilesInRootTest extends AuditTestTestBase {


    @Rule
    public final TemporaryFolder _temporaryFolder = new TemporaryFolder();

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
        assertEquals(LibOutcome.ROOT_NOT_FOUND, errors.get(0).getOutcome())  ;
    }

    @Test
    public void NonExDirWithContentFails() throws IOException {
        String fileRoot = (new FileSequenceBuilder( _temporaryFolder))
                .BuildFilesOnly()
                .getAbsolutePath();

        logger.debug("Testing {} on {}", "NonExDirWithContentFails", fileRoot);
        NoFilesInRoot st = new NoFilesInRoot(logger);
        st.setParams(fileRoot);
        st.LaunchTest();

        // Should fail
        assertTrue(st.IsTestFailed());

        // should be as many errors as files in the synthetic directory
        TestResult tr = st.getTestResult();
        ArrayList<TestMessage> errors = tr.getErrors();
        assertEquals(13, errors.size());
        TestMessage tm = errors.get(0);
        assertEquals(LibOutcome.FILES_IN_MAIN_FOLDER,tm.getOutcome());

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