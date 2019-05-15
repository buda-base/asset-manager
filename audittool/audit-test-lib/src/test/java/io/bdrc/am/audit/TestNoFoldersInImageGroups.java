package io.bdrc.am.audit;


import io.bdrc.am.audit.audittests.LibOutcome;
import io.bdrc.am.audit.audittests.NoFoldersInImageGroups;
import io.bdrc.am.audit.iaudit.TestMessage;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestNoFoldersInImageGroups extends AuditTestTestBase{


    @Rule
    public final TemporaryFolder rootFolder = new TemporaryFolder();


   FileSequenceBuilder _IGPfileSequenceBuilder;

    private final Hashtable<String, String> _emptySequenceTestParams = new Hashtable<>();
    private final Hashtable<String,String> _igParentsTestParams = new Hashtable<String,String>() {{
            put("ArchiveImageGroupParent", "harkeBeepar0lYn");
            put("DerivedImageGroupParent", "SchmengUndDreck");
        }};


    @Before
    public void CreateFileBuilders() {

        // Build a synthetic work structure, with two image group parents
        _IGPfileSequenceBuilder = new FileSequenceBuilder(rootFolder,_igParentsTestParams.values());
    }

    @Test
    public void TestPassingFiles() throws IOException {
        File fileRoot =  _IGPfileSequenceBuilder.BuildPassingFiles();
        NoFoldersInImageGroups fst = runTest(fileRoot.getAbsolutePath(),_igParentsTestParams);

        assertTrue("Test did not pass when it should",fst.IsTestPassed());
    }


    @Test
    public void TestFailingFiles() throws IOException {
        File fileRoot =  _IGPfileSequenceBuilder.BuildPassingFiles();
        AddFoldersToImageGroups(fileRoot,_igParentsTestParams);
        NoFoldersInImageGroups fst = runTest(fileRoot.getAbsolutePath(),_igParentsTestParams);

        assertTrue("Test did not fail when it should",fst.IsTestFailed());

        // There should be one error for each folder in each subgroup. That's 16.
        // The first outcome should be a summary for the first folder
        TestResult tr = fst.getTestResult();
        ArrayList<TestMessage> testMessages = tr.getErrors();
        assertEquals(16,testMessages.size());
        assertEquals(LibOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER,testMessages.get(0).getOutcome());

    }



    /**
     * This tests a known success path
     */
    @Test
    @Ignore
    public void TestSomething()  {
         Hashtable<String,String> _activeSequenceTestParams = new Hashtable<String,String>() {{
            put("ArchiveImageGroupParent", "archive");
            put("DerivedImageGroupParent", "image");
        }};
        NoFoldersInImageGroups fst = runTest("/Users/jimk/tmp/AuditToolTestData/W1KG11900", _activeSequenceTestParams);
        TestResult tr = fst.getTestResult();
        Assert.assertFalse("Test should have failed", tr.Passed());

    }

    @Test
    public void TestNotExist() {

        NoFoldersInImageGroups st = runTest("/MrMxyzptlk",_igParentsTestParams);
        assertTrue(st.IsTestFailed());
        TestResult tr = st.getTestResult();
        ArrayList<TestMessage> errors = tr.getErrors();

        assertEquals(1, errors.size());
        assertEquals(LibOutcome.ROOT_NOT_FOUND, errors.get(0).getOutcome())  ;
    }


    @Test
    public void setPath() {
        final String whanThatAprille = "WhanThatAprille";
        NoFoldersInImageGroups st = new NoFoldersInImageGroups(logger);
        st.setParams(whanThatAprille,_emptySequenceTestParams);
        assertEquals(st.getPath(),whanThatAprille);
    }

    private NoFoldersInImageGroups runTest(String path,Hashtable<String,String> igParents ) {
        NoFoldersInImageGroups st = new NoFoldersInImageGroups(logger);
        st.setParams(path, igParents);
        st.LaunchTest();

        return st;
    }

    /**
     * Creates test collateral to fail
     * @param fileRoot root of test
     * @param igParents Names of image group parents
     */
    private void  AddFoldersToImageGroups(File fileRoot, Hashtable<String,String>  igParents) throws IOException {

        // Creating the filter
        DirectoryStream.Filter<Path> filter = entry -> (entry.toFile().isDirectory());

        for(String igParent : igParents.values()) {

            // Get all the directories in the image group parent
            for (Path imageGroup :
                    Files.newDirectoryStream(Paths.get(fileRoot.getAbsolutePath(), igParent), filter)) {
                Files.createDirectories(Paths.get(imageGroup.toString(),"thisWillFailTheFolder"));
            }

        }



        }
}


