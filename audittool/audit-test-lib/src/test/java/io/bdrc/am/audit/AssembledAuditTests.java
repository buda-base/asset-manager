package io.bdrc.am.audit;


import io.bdrc.am.audit.audittests.FileSequence;
import io.bdrc.am.audit.iaudit.IAuditTest;
import io.bdrc.am.audit.iaudit.Outcome;
import io.bdrc.am.audit.iaudit.TestResult;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;

/**
 * Class for testing with existing data
 * Ignored because only works on a MacOS when /Volumes/Archive is mounted.
 */
@Ignore
@RunWith(Parameterized.class)

public class AssembledAuditTests {


    //  Make these public to work with a parameterless constructor  @Parameterized.Parameter(0)
    private final String testDirectory;

    //    @Parameterized.Parameter(1)
    private final Boolean expectedResult;

    private final Hashtable<String,String> properties;

    /*  ------ Arrange      --------- */
    public AssembledAuditTests(String pTestDirectory, Boolean pExpectedResult, Hashtable<String,String>  pProperties) {
        testDirectory = pTestDirectory;
        expectedResult = pExpectedResult;
        properties = pProperties;
    }

    private IAuditTest fileSeqTest;

    @Before
    public void setup() {
        fileSeqTest = new FileSequence();
    }

    // Parameters annotation marks this as data generator
    @Parameterized.Parameters
    public static Collection Directories() {
        Hashtable<String,String> properties = new Hashtable<String,String>() {
            {
             put("ArchiveImageGroupParent","archive");
             put("DerivedImageGroupParent","images");
            }
        };

        // dont use user-specific test directories
        return Arrays.asList(new Object[][]{
                {"/Volumes/Archive/W1KG11900",  true,  properties},
//                {"/Users/jimk/tmp/AuditToolTestData/W1KG11900", true, properties},
                {"/Volumes/Archive/W1KG13585", true, properties},
//                {"/Users/jimk/tmp/AuditToolTestData/W1KG13585", true, properties}
        });
    }


    /* --------- Act -------- */
@Test
    public void testFileSequence() {
        System.out.println(" Testing " + testDirectory);
        fileSeqTest.setParams(testDirectory, properties);
        StopWatch sw = StopWatch.createStarted();
        fileSeqTest.LaunchTest();
        sw.stop();
        TestResult tr = fileSeqTest.getTestResult();

        treeCount tc = getTreeCount(testDirectory);
        System.out.println(String.format("files: %d dirs: %d sec %s", tc.nFiles, tc.nDirs, sw.toString()));
        System.out.println(String.format("outcome %s number of failures %d", tr.getOutcome(), tr.getErrors().size()));

        Boolean actualResult = tr.getOutcome() == Outcome.PASS ;
        Assert.assertEquals(expectedResult,actualResult);
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
