package io.bdrc.am.audit.shell;

import io.bdrc.am.audit.iaudit.*;
import io.bdrc.am.audit.iaudit.message.TestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;


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
    private static final String TEST_DICT_PROPERTY_NAME = "testDictionaryClassName";
    private static final String TEST_LOGGER_HEADER = "id,test_name,outcome,error_number,error_test,detail_path\n";

    // should get thing2 whose name is io.bdrc.am.audit.shell.shell
    private final static Logger sysLogger = LoggerFactory.getLogger("sys"); // shellLogger.name=shellLogger //("root");
    private final static Logger detailLogger = LoggerFactory.getLogger("detailLogger"); //("root");
    private final static Logger testResultLogger = LoggerFactory.getLogger("testResultLogger");

    private final static int SYS_OK = 0;
    private final static int SYS_ERR = 1;

    private static AuditTestLogController testLogController;

    public static void main(String[] args) {

        Boolean anyFailed = false;

        try
        {


            Path resourceFile = resolveResourceFile("shell.properties");
            FilePropertyManager shellProperties = new FilePropertyManager(resourceFile.toAbsolutePath().toString());

            Hashtable<String, AuditTestConfig> td;

            td = LoadDictionaryFromProperty("testJar", shellProperties);

            assert td != null;
            ArgParser argParser = new ArgParser(args);

            testLogController = BuildTestLog(argParser, testResultLogger, TEST_LOGGER_HEADER);

            if (argParser.has_Dirlist())
            {
                ArrayList<String> dirsToTest = argParser.getDirs();
                ResolvePaths(dirsToTest);


                for (String aTestDir : dirsToTest)
                {
                    sysLogger.debug("arg =  {} ", aTestDir);
                    Boolean onePassed = RunTestsOnDir(shellProperties, td, aTestDir);
                    anyFailed |= !onePassed;
                }
            }

            // dont force mutually exclusive. Why not do both?
            if (argParser.getReadStdIn())
            {
                String curLine;
                try (BufferedReader f = new BufferedReader(new InputStreamReader(System.in)))
                {
                    while (null != (curLine = f.readLine()))
                    {
                        sysLogger.debug("readLoop got line {} ", curLine);

                        testLogController.ChangeAppender(Paths.get(curLine).getFileName().toString());
                        Boolean onePassed = RunTestsOnDir(shellProperties, td, curLine);
                        anyFailed |= !onePassed;
                    }
                }
            }

        } catch (Exception e)
        {
            System.out.println("Exiting on exception " + e.getMessage());
            sysLogger.error(e.toString(), e, "Exiting on Exception", "Fail");
            System.exit(SYS_ERR);
        }


        sysLogger.trace("Exiting all pass? {}", String.valueOf(!anyFailed));
        System.exit(anyFailed ? SYS_ERR : SYS_OK );
    }

    private static AuditTestLogController BuildTestLog(final ArgParser ap, Logger parentLogger, String csvHeader)
    {
        AuditTestLogController tlc;
        String logDir = ap.getLogDirectory();
        tlc = new AuditTestLogController();
        tlc.setCsvHeader(csvHeader);
        tlc.setTestResultLogger(parentLogger.getName());

        tlc.setAppenderDirectory(logDir);

        return tlc;
    }

    /**
     * Set up, run all tests against a folder.
     * @param shellProperties environment, used for resolving test arguments
     * @param testSet dictionary of tests
     * @param aTestDir test subject
     * @return If all the tests passed or not
     */
    private static Boolean RunTestsOnDir(final FilePropertyManager shellProperties,
                                         final Hashtable<String, AuditTestConfig> testSet, final String aTestDir)
    {

        // testLogController ctor sets test log folder
        testLogController.ChangeAppender(BuildTestLogFileName(aTestDir));

        Boolean anyFailed = false;
        for (String testName : testSet.keySet())
        {

            AuditTestConfig testConfig = testSet.get(testName);

            // Do we have a value at all?
            if (testConfig == null)
            {

                // sysLogger goes to a csv and a log file, so add the extra parameters.
                // log4j wont care.
                sysLogger.error("No test config found for {}. Contact library provider.",
                        testName, "No test config found", "Failed");
                anyFailed = true;
                continue;
            }

            // Is this test an  IAuditTest?
            Class<?> testClass = testConfig.getTestClass();
            if (!IAuditTest.class.isAssignableFrom(testClass))
            {
                sysLogger.error("Test found for {} does not implement IAudit", testName, "doesnt implement IAudit",
                        "Failed");
                anyFailed = false;
                continue;
            }

            // ? getName() always runs logger to console
            Logger testLogger = LoggerFactory.getLogger(testClass);

            // descriptive
            String testDesc = testConfig.getFullName();

            // extract the property values the test needs
            Hashtable<String, String> propertyArgs = ResolveArgNames(testConfig.getArgNames(), shellProperties);

            @SuppressWarnings("unchecked")
            Boolean onePassed = TestOnDirPassed((Class<IAuditTest>) testClass, testLogger, testDesc, propertyArgs,
                    aTestDir);
            anyFailed |= !onePassed;
        }
        return !anyFailed;
    }

    /**
     * Create the file out of a parameter and the date, formatted yyyy-mm-dd-hh.mm
     * @param aTestDir full name of folder
     */
    private static String  BuildTestLogFileName(final String aTestDir) {
        DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("-yyyy-MM-dd.kk.mm")
                                         .withLocale(Locale.getDefault())
                                         .withZone(ZoneId.systemDefault());

        String fileDate = dtf.format(Instant.now());
        return Paths.get(aTestDir).getFileName().toString() + fileDate+ ".csv";
    }

    private static Boolean TestOnDirPassed(final Class<IAuditTest> testClass, final Logger testLogger,
                                           final String testDesc, final Hashtable<String, String> propertyArgs,
                                           final String testDir)
    {
        sysLogger.debug("Invoking {}. Params :{}:", testDesc, testDir);

        @SuppressWarnings("unchecked")
        TestResult tr = null;
        try
        {
            tr = RunTest(testLogger, testClass, testDir, propertyArgs);

            // String resultLogFormat = "Result:%10s\tFolder:%20s\tTest:%30s";
            String resultLogFormat = "{}\t{}\t\t{}";

            String workName = Paths.get(testDir).getFileName().toString();
            String testResultLabel = tr.Passed() ? "Passed" : "Failed";

            if (tr.Passed())
            {
                sysLogger.info(resultLogFormat, testResultLabel, testDir, testDesc);
            }
            else
            {
                sysLogger.error(resultLogFormat, testResultLabel, testDir, testDesc);
            }

            // Test result logger doesn't have levels
            // See testLogController.setSVCFormat above. Provide params for all
            // headings. In CSV format, first arg is ignored.
            //"id,test_name,outcome,detail_path,error_number,error_test"
            testResultLogger.info("ignoredCSV", workName, testDesc, testResultLabel, null, null, testDir);

            for (TestMessage tm : tr.getErrors())
            {
                detailLogger.error("{}:{}:{}", tm.getOutcome().toString(), tm.getMessage(), testDir);

                // We don't repeat the first few columns for detailed errors
                // testResultLogger also has no level.
                testResultLogger.info("ignoredCSV", null, null, null, tm.getOutcome().toString(), tm.getMessage(),
                        testDir);
            }
        } catch (Exception e)
        {
            System.out.println(String.format("%s %s", testDir, testClass.getCanonicalName()));
            e.printStackTrace();
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

    @SuppressWarnings({"unchecked"})
    private static Hashtable<String, AuditTestConfig> LoadDictionaryFromProperty(final String testJarPropertyName,
                                                                                 FilePropertyManager resources) throws Exception
    {
        String loc = "LoadDictionary";

        sysLogger.trace( "entering {}",loc);
        String jarPath = System.getProperty(testJarPropertyName);
        if (jarPath == null)
        {
            String message = String.format("%s property not found", testJarPropertyName);
            sysLogger.error(message);
            throw new Exception(message);
        }
        if (!(new File(jarPath)).isFile())
        {
            throw new FileNotFoundException(jarPath);
        }
        Hashtable<String, AuditTestConfig> result = null;

        String libUrlStr = "jar:file:" + jarPath + "!/";

        ClassLoader loader;
        try
        {
            URL libUrl = new URL(libUrlStr);
            sysLogger.debug("Seeking libUrl {}", libUrl);
            loader = URLClassLoader.newInstance(
                    new URL[]{libUrl});
            if (loader == null)
            {
                sysLogger.error("loader null");
            }
            else
            {
                sysLogger.debug("loader got: {}", loader.getClass().getCanonicalName());
            }

        } catch (MalformedURLException e) {
            sysLogger.error(libUrlStr, e);
            throw new Exception(String.format("libURL :%s: not Found ", libUrlStr));
        }
        catch (Exception e) {
            sysLogger.error(libUrlStr,e);
            throw new Exception(String.format("libURL :%s: threw exception %s",libUrlStr,e.getMessage()));
        }


        String tdClassName =
                resources.getPropertyString(shell.TEST_DICT_PROPERTY_NAME);
        sysLogger.debug("{} value of property :{}:",shell.TEST_DICT_PROPERTY_NAME,tdClassName);

        try
        {
            if (loader != null)
            {

                Class<IAuditTest> testDict = (Class<IAuditTest>) Class.forName(tdClassName, true, loader); //, loader);
                Object instance = testDict.newInstance();
                Method method = testDict.getDeclaredMethod("getTestDictionary");

                result = (Hashtable<String, AuditTestConfig>) method.invoke(instance);
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e)
        {
            String eStr = e.toString();
            sysLogger.error(eStr, eStr, " Cant acquire resource file", "Failed");
        }
        catch (Exception e2)
        {
            String eStr = e2.toString();
            sysLogger.error("Other Exception ", e2);
            throw e2;
        }

        sysLogger.trace("leaving {} result non-null? {}", loc, String.valueOf(result != null));
        return result;
    }

    /**
     * In place replacement of paths with their resolved value
     *
     * @param resolveDirs list of paths to resolve
     */
    private static void ResolvePaths(final ArrayList<String> resolveDirs) {
        for (int i = 0; i < resolveDirs.size(); i++)
        {
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
        try
        {
            Constructor<IAuditTest> ctor = testClass.getConstructor(Logger.class);
            IAuditTest inst = ctor.newInstance(testLogger);

            inst.setParams((Object[]) params);
            inst.LaunchTest();

            tr = inst.getTestResult();

        } catch (Exception eek)
        {
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

        if ((resHome == null) || resHome.isEmpty())
        {
            sysLogger.debug("resolveResourceFile: atHome empty.");
            resHome = System.getenv("ATHOME");
            sysLogger.debug("resolveResourceFile: getenv ATHOME {}", resHome);
        }
        if ((resHome == null) || resHome.isEmpty())
        {
            resHome = System.getProperty("user.dir");
            sysLogger.debug("resolveResourceFile: getenv user.dir {}", resHome);
        }
        sysLogger.debug("Reshome is {} ", resHome, " is resource home path");
        return Paths.get(resHome, resourceFileName);
    }
}
