package io.bdrc.am.audit;

import io.bdrc.am.audit.audittests.FileSequence;
import io.bdrc.am.audit.iaudit.IAuditTest;
import io.bdrc.am.audit.iaudit.TestResult;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * Class for testing with existing data
 */
@RunWith(Parameterized.class)
public class AssembledAuditTests {


    //    @Parameterized.Parameter(0)
    private String testDirectory;

    //    @Parameterized.Parameter(1)
    private Boolean expectedResult;

    /*  ------ Arrange      --------- */
    public AssembledAuditTests(String pTestDirectory, Boolean pExpectedResult) {
        testDirectory = pTestDirectory;
        expectedResult = pExpectedResult;
    }

    private IAuditTest fileSeqTest;

    @Before
    public void setup() {
        fileSeqTest = new FileSequence();
    }

    // Parameters annotation marks this as data generator
    @Parameterized.Parameters(name = "Run test of {0} expect {1}")
    public static Collection Directories() {
        return Arrays.asList(new Object[][]{
                {"/Volumes/Archive/W1KG11900", true},
                {"/Users/jimk/tmp/AuditToolTestData/W1KG11900", true},
                {"/Volumes/Archive/W1KG13585", true},
                {"/Volumes/Archive/W1KG11900", true},
                {"/Users/jimk/tmp/AuditToolTestData/W1KG13585", true}
        });
    }


    /* --------- Act -------- */
@Test
    public void testFileSequence() {
        System.out.println(" Testing " + testDirectory);
        fileSeqTest.setParams(testDirectory);
        StopWatch sw = StopWatch.createStarted();
        fileSeqTest.LaunchTest();
        sw.stop();
        TestResult tr = fileSeqTest.getTestResult();

        treeCount tc = getTreeCount(testDirectory);
        System.out.println(String.format("files: %d dirs: %d sec %s", tc.nFiles, tc.nDirs, sw.toString()));
    }


    class treeCount {
        Integer nFiles = 0;
        Integer nDirs = 0;
    }

    private treeCount getTreeCount(String path) {
        treeCount tc = new treeCount();
        innerGetTreeCount(tc, path);
        return tc;
    }

    private void innerGetTreeCount(treeCount tc, String path) {
        File f = new File(path);
        File[] files = f.listFiles();
        if (files == null) {
            return;
        }
        for (File eachFile : files) {
            if (eachFile.isDirectory()) {
                tc.nDirs++;
                innerGetTreeCount(tc, eachFile.getAbsolutePath());
            } else {
                tc.nFiles++;
            }
        }
    }


}
