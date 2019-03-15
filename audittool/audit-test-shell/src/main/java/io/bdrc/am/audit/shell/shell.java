package io.bdrc.am.audit.shell;

import io.bdrc.am.audit.iaudit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * main class for shell. See usage. Must have a -DtestJar='path to jar containing tests'
 * <p>
 * The test jar must contain a class named in the /shell.resources file with the key
 * testDictionaryClassName.
 * ex:
 * testDictionaryClassName = TestDictionary
 * The test dictionary class must expose a public method named getTestDictionary()
 * which returns a Hashset<String, Class>.
 * <p>
 * The tests in the Hashset's values must implement the io.bdrc.am.audit.iaudit.IAudit interface.
 */
public class shell {


/*
  Call with an implementation of audit test library in the
  classpath or with the -DtestJar:<pathToTestJar>
  @param args  See usage
  */

    /**
     * Property key to find class name of test dictionary
     */
    private static final String testDictPropertyName = "testDictionaryClassName";

    // should get thing2 whose name is io.bdrc.am.audit.shell.shell
    private static Logger sysLogger = LoggerFactory.getLogger(shell.class); //("root");

    public static void main(String[] args) {


        Hashtable<String, Class<IAuditTest>> td = null;
        try {
            td = LoadDictionaryFromProperty("testJar");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the jar to examine
        ArrayList<String> dirsToTest = (new ArgParser(args)).getDirs();

        assert td != null;
        assert td.keySet() != null;
        for (String testName : td.keySet()) {

            Class testClass = td.get(testName);
            if (testClass == null) {
                sysLogger.error("Test found for {} does not have a Class Entry", testName);
                continue;
            }
            if (!IAuditTest.class.isAssignableFrom(testClass)) {
                sysLogger.error("Test found for {} does not implement IAudit", testName);
                continue;
            }

            Logger testLogger = LoggerFactory.getLogger(td.get(testName));

            ResolvePaths(dirsToTest);

            // Cant use Object[] directly
            String[] runArgs = new String[dirsToTest.size()];
            dirsToTest.toArray(runArgs);

            for (String aTestDir : dirsToTest) {
                sysLogger.info("Test {} invoked. Params :{}:", testName, aTestDir);
                TestResult tr = RunTest(testLogger, (Class<IAuditTest>) testClass, aTestDir);

                for (TestMessage tm : tr.getErrors()) {
                    sysLogger.error("{}:{}", tm.getOutcome().toString(), tm.getMessage());
                }
                String resultLogString = String.format("Test %s result %s", testName, tr.Passed() ? "Passed" : "Failed");
                if (tr.Passed()) {
                    sysLogger.info(resultLogString);
                } else {
                    sysLogger.error(resultLogString);
                }
            }

        }
    }

    private static Hashtable<String, Class<IAuditTest>> LoadDictionaryFromProperty(final String testJarPropertyName) throws IOException {
        String jarPath = System.getProperty(testJarPropertyName);


        if (!(new File(jarPath)).isFile()) {
            throw new FileNotFoundException(jarPath);
        }
        Hashtable<String, Class<IAuditTest>> result = null;

        String libUrlStr = "jar:file:" + jarPath + "!/";

        ClassLoader loader;
        try {
            URL libUrl = new URL(libUrlStr);
            loader = URLClassLoader.newInstance(
                    new URL[]{libUrl});
        } catch (MalformedURLException e) {
            sysLogger.error(libUrlStr, e);
            return result;
        }

        String tdClassName = new FilePropertyManager("shell.properties").getPropertyString(shell.testDictPropertyName);

        try {
            if (loader != null) {
                Class<IAuditTest> testDict = (Class<IAuditTest>) Class.forName(tdClassName, true, loader); //, loader);
                Object instance = testDict.newInstance();
                Method method = testDict.getDeclaredMethod("getTestDictionary");
                result = (Hashtable<String, Class<IAuditTest>>) method.invoke(instance);
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            sysLogger.error(e.toString());
        }

        return result;
    }

    /**
     * In place replacement of paths with their resolved value
     *
     * @param resolveDirs list of paths to resolve
     */
    private static void ResolvePaths(final ArrayList<String> resolveDirs) {
        for (int i = 0; i < resolveDirs.size(); i++) {
            resolveDirs.set(i, Paths.get(resolveDirs.get(i)).toAbsolutePath().toString());
        }
    }


    /**
     * Shell to run a test instance, given its class
     *
     * @param testLogger Logger for the test. Not the same as the shell logger
     * @param params     parameters to the
     */
    private static TestResult RunTest(Logger testLogger, Class<IAuditTest> testClass, String... params) {

        String className = testClass.getCanonicalName();

        TestResult tr = new TestResult();
        try {
            Constructor<IAuditTest> ctor = testClass.getConstructor(Logger.class);
            IAuditTest inst = ctor.newInstance(testLogger);

            inst.setParams((Object[]) params);
            inst.LaunchTest();

            tr = inst.getTestResult();

        } catch (Exception eek) {
            testLogger.error(" Exception {} when running test {}", eek, className);
            tr.setOutcome(Outcome.FAIL);
            tr.AddError(Outcome.SYS_EXC, eek.toString());
        }
        return tr;

    }
}
