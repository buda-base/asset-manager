package io.bdrc.audit.audittests;


import io.bdrc.audit.audittests.AuditTestTestBase;
import io.bdrc.audit.iaudit.AuditTestConfig;
import io.bdrc.audit.iaudit.IAuditTest;
import io.bdrc.audit.iaudit.LibOutcome;
import io.bdrc.audit.iaudit.TestResult;
import org.junit.Assert;
import org.junit.Ignore;
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

    private final String jarUrlStr = "file:///Users/jimk/dev/" +
            "asset-manager/audittool/" +
            "test-lib/target/" +
            "audit-test-lib-1.0-SNAPSHOT-jar-with-dependencies.jar";

    private final String dictName = "TestDictionary";

    /**
     * validateForShell is a test that validates that the shell will be able to locate testDictionary
     * in this jar
     * @Ignore because this test needs to pass before the assembly which it references is
     * actually built.
     *
     * @throws URISyntaxException    Requires a well formed URI
     * @throws MalformedURLException Which transforms into a well formed URL
     */
    @Test
    @Ignore
    public void TestForShellCallable() throws URISyntaxException, MalformedURLException {
        URL libUrl = (new URI(jarUrlStr)).toURL();

        Hashtable<String, AuditTestConfig> libTests = getTestDictionary(libUrl, dictName);

        Assert.assertNotNull(libTests);
        Assert.assertEquals("Number of tests doesnt match", 8, libTests.size());
    }

    @Test
    @Ignore
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
    @Ignore
    public void TestRunnable() throws URISyntaxException, MalformedURLException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException
    {
        URL libUrl = (new URI(jarUrlStr)).toURL();
        final Hashtable<String, String> emptySequenceTestParams = new Hashtable<>();
        // endregion

        Hashtable<String, AuditTestConfig> libTests = getTestDictionary(libUrl, dictName);

        for (AuditTestConfig c : libTests.values()) {
            // debug System.out.println(MessageFormat.format("testing {0}",c.getKey()));
            IAuditTest thisTest =
                    (IAuditTest) (c.getTestClass().getDeclaredConstructor(Logger.class, String.class).newInstance(logger,
                            c.getKey()));
            thisTest.setParams("/ImNotHere", emptySequenceTestParams);
            thisTest.LaunchTest();
            TestResult tr = thisTest.getTestResult();
            Assert.assertNotNull(tr);

            Assert.assertEquals(String.format("Test %s unexpected rc", thisTest.getTestName()),
                    LibOutcome.ROOT_NOT_FOUND, tr.getErrors().get(0).getOutcome());
        }
    }
}
