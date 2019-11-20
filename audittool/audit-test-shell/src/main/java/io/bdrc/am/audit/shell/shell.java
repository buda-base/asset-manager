package io.bdrc.am.audit.shell;

import io.bdrc.am.audit.iaudit.*;
import io.bdrc.am.audit.iaudit.message.TestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


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
    private final static Logger sysLogger = LoggerFactory.getLogger("con"); // shellLogger.name=con //("root");
    //    private final static Logger summaryLogger = LoggerFactory.getLogger("summaryLogger"); //("root");
    // private final static Logger detailLogger = LoggerFactory.getLogger("detailLogger"); //("root");

    private final static int SYS_OK = 0 ;
    private final static int SYS_ERR = 1 ;

    public static void main(String[] args) {

        Boolean anyFailed = false;

        try {



            Path resourceFile = resolveResourceFile("shell.properties");
            FilePropertyManager shellProperties = new FilePropertyManager(resourceFile.toAbsolutePath().toString());

            Hashtable<String, AuditTestConfig> td ;

            td = LoadDictionaryFromProperty("testJar", shellProperties);

            assert td != null;
            ArgParser argParser = new ArgParser(args);

            if (argParser.has_Dirlist()) {
                ArrayList<String> dirsToTest = argParser.getDirs();
                ResolvePaths(dirsToTest);
                for (String aTestDir : dirsToTest) {
                    Boolean onePassed =  RunTestsOnDir(shellProperties, td, aTestDir);
                    anyFailed |= !onePassed;
                }
            }

            // dont force mutually exclusive. Why not do both?
            if (argParser.getReadStdIn()) {
                String curLine;
                try (BufferedReader f = new BufferedReader(new InputStreamReader(System.in))) {
                    while (null != (curLine = f.readLine())) {
                        sysLogger.debug("readLoop got line {} ", curLine);
                        Boolean onePassed = RunTestsOnDir(shellProperties, td, curLine);
                        anyFailed |= !onePassed;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Exiting on exception " + e.getMessage());
            sysLogger.error(e.toString(), e, "Exiting on Exception", "Fail");
            System.exit( SYS_ERR) ;
        }

        System.exit(anyFailed ? SYS_ERR : SYS_OK );
    }

    private static Boolean RunTestsOnDir(final FilePropertyManager shellProperties, final Hashtable<String, AuditTestConfig>
            td, final String aTestDir)
    {

        Boolean anyFailed = false;
        for (String testName : td.keySet()) {

            AuditTestConfig testConfig = td.get(testName);

            // Do we have a value at all?
            if (testConfig == null) {

                // sysLogger foes to a csv and a log file, so add the extra parameters.
                // log4j wont care.
                sysLogger.error("No test config found for {}. Contact library provider.",
                        testName, "No test config found", "Failed");
                anyFailed = true;
                continue;
            }

            // Is this test an  IAuditTest?
            Class<?> testClass = testConfig.getTestClass();
            if (!IAuditTest.class.isAssignableFrom(testClass)) {
                sysLogger.error("Test found for {} does not implement IAudit", testName, "doesnt implement IAudit",
                        "Failed");
                anyFailed = false;
                continue;
            }

            Logger testLogger = LoggerFactory.getLogger(testClass);

            // descriptive
            String testDesc = testConfig.getFullName();

            // extract the property values the test needs
            Hashtable<String, String> propertyArgs = ResolveArgNames(testConfig.getArgNames(), shellProperties);

            Boolean onePassed = TestOnDirPassed((Class<IAuditTest>) testClass, testLogger, testDesc, propertyArgs,
                    aTestDir);
            anyFailed |= !onePassed;
        }
        return !anyFailed;
    }

    private static Boolean TestOnDirPassed(final Class<IAuditTest> testClass, final Logger testLogger, final String
            testDesc, final Hashtable<String, String> propertyArgs, final String testDir)
    {
        sysLogger.debug("Invoking {}. Params :{}:", testDesc, testDir);


        @SuppressWarnings("unchecked")
        TestResult tr = RunTest(testLogger, testClass, testDir, propertyArgs);

        for (TestMessage tm : tr.getErrors()) {
            // detailLogger.error("{}:{}:{}", testDir, tm.getOutcome().toString(), tm.getMessage());
        }
        String resultLogFormat = "folder:{}\tTest:{}\tresult:{}";
        if (tr.Passed()) {
            sysLogger.info(resultLogFormat, testDir, testDesc, "Passed");
        } else {
            sysLogger.error(resultLogFormat, testDir, testDesc, "Failed");
        }
        return tr.Passed();
    }

    /**
     * build dictionary of property arguments, pass to each test
     *
     * @param argNames        collection of properties to find
     * @param propertyManager handles property lookup
     * @return copy of argNames with found values added:  argNames[x]+property value
     */
    private static Hashtable<String, String> ResolveArgNames(final List<String> argNames,
                                                             PropertyManager propertyManager)
    {
        Hashtable<String, String> argNames1 = new Hashtable<>();
        argNames.forEach((String t) -> argNames1.put(t, propertyManager.getPropertyString(t)));

        return argNames1;
    }

    @SuppressWarnings("unchecked")
    private static Hashtable<String, AuditTestConfig> LoadDictionaryFromProperty(final String testJarPropertyName,
                                                                                 FilePropertyManager resources) throws Exception
    {

        String jarPath = System.getProperty(testJarPropertyName);
        if (jarPath == null) {
            String message = String.format("%s property not found", testJarPropertyName);
            throw new Exception(message);
        }
        if (!(new File(jarPath)).isFile()) {
            throw new FileNotFoundException(jarPath);
        }
        Hashtable<String, AuditTestConfig> result = null;

        String libUrlStr = "jar:file:" + jarPath + "!/";

        ClassLoader loader;
        try {
            URL libUrl = new URL(libUrlStr);
            loader = URLClassLoader.newInstance(
                    new URL[]{libUrl});
        } catch (MalformedURLException e) {
            sysLogger.error(libUrlStr, e);
            throw new Exception(String.format("%s libURL not found", libUrlStr));
        }


        String tdClassName =
                resources.getPropertyString(shell.testDictPropertyName);

        try {
            if (loader != null) {

                Class<IAuditTest> testDict = (Class<IAuditTest>) Class.forName(tdClassName, true, loader); //, loader);
                Object instance = testDict.newInstance();
                Method method = testDict.getDeclaredMethod("getTestDictionary");

                result = (Hashtable<String, AuditTestConfig>) method.invoke(instance);
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            String eStr = e.toString();
            sysLogger.error(eStr, eStr, " Cant acquire resource file", "Failed");
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
     * RunTest
     * Shell to run a test instance, given its class
     *
     * @param testLogger Logger for the test. Not the same as the shell logger
     * @param params     array of additional parameters. Caller has to prepare it for each test. (Needs more structure)
     */
    private static TestResult RunTest(Logger testLogger, Class<IAuditTest> testClass, Object... params) {

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

    /**
     * Gets the working directory of the executable
     * invoke with -DatHome="someDirectorySpec"
     * if -DatHome not given, looks up environment variable ATHOME
     * if that's empty, uses "user.dir"
     */
    private static Path resolveResourceFile(String resourceFileName) {
        String resHome = System.getProperty("atHome");
        if ((resHome == null) || resHome.isEmpty()) {
            resHome = System.getenv("ATHOME");
        }
        if ((resHome == null) || resHome.isEmpty()) {
            resHome = System.getProperty("user.dir");
        }
        sysLogger.debug("Reshome is {} ", resHome, " is resource home path");
        return Paths.get(resHome, resourceFileName);
    }
}
