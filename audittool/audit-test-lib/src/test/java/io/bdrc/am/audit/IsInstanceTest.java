package io.bdrc.am.audit;


import io.bdrc.am.audit.audittests.LibOutcome;
import io.bdrc.am.audit.iaudit.IAuditTest;
import io.bdrc.am.audit.iaudit.AuditTestConfig;
import io.bdrc.am.audit.iaudit.Outcome;
import io.bdrc.am.audit.iaudit.TestResult;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


import java.util.Hashtable;

/**
 * The type Is instance test.
 */
public class IsInstanceTest extends AuditTestTestBase {

    private final String jarUrlStr = "file:///Users/jimk/dev/AssetManager/" +
            "asset-manager/audittool/" +
            "audit-test-lib/target/" +
            "audit-test-lib-1.0-SNAPSHOT-jar-with-dependencies.jar";

    private final String dictName = "io.bdrc.am.audit.audittests.TestDictionary";

    /**
     * validateForShell is a test that validates that the shell will be able to locate testDictionary
     * in this jar
     *
     * @throws URISyntaxException    Requires a well formed URI
     * @throws MalformedURLException Which transforms into a well formed URL
     */
    @Test
    public void TestForShellCallable() throws URISyntaxException, MalformedURLException {
        URL libUrl = (new URI(jarUrlStr)).toURL();

        Hashtable<String, AuditTestConfig> libTests = getTestDictionary(libUrl, dictName);

        Assert.assertNotNull(libTests);
        Assert.assertEquals("Number of tests doesnt match", 5, libTests.size());
    }

    @Test
    public void TestInterfaceCallable() throws URISyntaxException, MalformedURLException {
        URL libUrl = (new URI(jarUrlStr)).toURL();
        Hashtable<String, AuditTestConfig> libTests = getTestDictionary(libUrl, dictName);

        for (AuditTestConfig atc : libTests.values()) {
            Class<?> c = atc.getTestClass();
            Assert.assertTrue(String.format("class %s doesnt implement IAuditTest",
                    c.getCanonicalName()), IAuditTest.class.isAssignableFrom(c));
        }
    }

    /**
     * Make sure every test Fails with root not found when given a bogus directory
     */
    @Test
    public void TestRunnable() throws URISyntaxException, MalformedURLException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException
    {
        URL libUrl = (new URI(jarUrlStr)).toURL();
        final Hashtable<String, String> emptySequenceTestParams = new Hashtable<>();
        // endregion

        Hashtable<String, AuditTestConfig> libTests = getTestDictionary(libUrl, dictName);

        for (AuditTestConfig c : libTests.values()) {
            IAuditTest thisTest =
                    (IAuditTest) (c.getTestClass().getDeclaredConstructor(Logger.class).newInstance(logger));
            thisTest.setParams("/ImNotHere", emptySequenceTestParams);
            thisTest.LaunchTest();
            TestResult tr = thisTest.getTestResult();
            Assert.assertNotNull(tr);
            Assert.assertSame(String.format("Test %s not expected", thisTest.getTestName()),
                    Outcome.FAIL, tr.getOutcome());
            Assert.assertEquals(String.format("Test %s not expected", thisTest.getTestName()),LibOutcome.ROOT_NOT_FOUND, tr.getErrors().get(0).getOutcome());
        }
    }
}
