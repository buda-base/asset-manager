package io.bdrc.am.audit.shell;

import io.bdrc.am.audit.iaudit.*;
import io.bdrc.am.audit.iaudit.message.TestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import static java.nio.file.Paths.get;


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
        Boolean onePassed ;

        try
        {
            sysLogger.trace("Entering main");

            Path resourceFile = resolveResourceFile("shell.properties");
            FilePropertyManager shellProperties = new FilePropertyManager(resourceFile.toAbsolutePath().toString());

            Hashtable<String, AuditTestConfig> td;

            sysLogger.trace("Parsing args");
            ArgParser argParser = new ArgParser(args);

            // Replaced with class
            TestJarLoader testJarLoader = new TestJarLoader();

            String tdClassName = shellProperties.getPropertyString(TEST_DICT_PROPERTY_NAME);
            sysLogger.debug("{} value of property :{}:", TEST_DICT_PROPERTY_NAME, tdClassName);
            td = testJarLoader.LoadDictionaryFromProperty("testJar", tdClassName);

            assert td != null;

            testLogController = BuildTestLog(argParser, TEST_LOGGER_HEADER);

            if (argParser.has_Dirlist())
            {
                for (String aTestDir : ResolvePaths(argParser.getDirs()))
                {
                    sysLogger.debug("arg =  {} ", aTestDir);
                    onePassed = RunTestsOnDir(shellProperties, td, aTestDir);
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
                        onePassed = RunTestsOnDir(shellProperties, td, curLine);
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
        System.exit(anyFailed ? SYS_ERR : SYS_OK);
    }

    private static AuditTestLogController BuildTestLog(final ArgParser ap, String csvHeader)
    {
        AuditTestLogController tlc;
        String logDir = ap.getLogDirectory();
        tlc = new AuditTestLogController();
        tlc.setCsvHeader(csvHeader);
        tlc.setTestResultLogger(shell.testResultLogger.getName());

        tlc.setAppenderDirectory(logDir);

        return tlc;
    }

    /**
     * Set up, run all tests against a folder.
     *
     * @param shellProperties environment, used for resolving test arguments
     * @param testSet         dictionary of tests
     * @param aTestDir        test subject
     * @return If all the tests passed or not
     */
    private static Boolean RunTestsOnDir(final FilePropertyManager shellProperties,
                                         final Hashtable<String, AuditTestConfig> testSet, final String aTestDir)
            throws IOException
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
        if (anyFailed)
        {
            testLogController.RenameLogFail();
        }
        else
        {
            testLogController.RenameLogPass();
        }
        return !anyFailed;
    }

    /**
     * Create the file out of a parameter and the date, formatted yyyy-mm-dd-hh.mm
     *
     * @param aTestDir full name of folder
     */
    private static String BuildTestLogFileName(final String aTestDir) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("-yyyy-MM-dd.kk.mm")
                                        .withLocale(Locale.getDefault())
                                        .withZone(ZoneId.systemDefault());

        String fileDate = dtf.format(Instant.now());
        return get(aTestDir).getFileName().toString() + fileDate + ".csv";
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

            String workName = get(testDir).getFileName().toString();

            String testResultLabel ;

            if (tr.getOutcome().equals(Outcome.SYS_EXC) || tr.getOutcome().equals(Outcome.FAIL))
            {
                testResultLabel = "Failed";
                sysLogger.error(resultLogFormat, testResultLabel, testDir, testDesc);
            }
            else if (tr.getOutcome().equals(Outcome.PASS))
            {
                testResultLabel =  "Passed";
                sysLogger.info(resultLogFormat, testResultLabel, testDir, testDesc);
            }
            else if (tr.getOutcome().equals(Outcome.NOT_RUN))
            {
                testResultLabel = "Not Run";
                sysLogger.warn(resultLogFormat, testResultLabel, testDir, testDesc);
            }
            else
            {
                testResultLabel = String.format("Unknown result status %d", tr.getOutcome());
                sysLogger.error(resultLogFormat, testResultLabel , testDir, testDesc);
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

        assert tr != null;
        return tr.Passed();

    }

    /**
     * build dictionary of property arguments, pass to each test
     *
     * @param argNames collection of properties to find
     * @param pm       property lookup
     * @return copy of argNames with found values added:  argNames[x]+property value
     */
    private static Hashtable<String, String> ResolveArgNames(final List<String> argNames,
                                                             PropertyManager pm)
    {
        Hashtable<String, String> argValues = new Hashtable<>();
        argNames.forEach((String t) -> argValues.put(t, pm.getPropertyString(t)));

        // Add global parameters
        String errorsAsWarning = pm.getPropertyString("ErrorsAsWarning");
        if ((errorsAsWarning != null) && !errorsAsWarning.isEmpty())
        {
            argValues.put("ErrorsAsWarning", errorsAsWarning);
        }

        return argValues;
    }

    /**
     * In place replacement of paths with their resolved value
     *
     * @param resolveDirs list of paths to resolve
     * @return entries fully qualified
     */
    private static List<String> ResolvePaths(List<String> resolveDirs) {
        List<String> outList = new ArrayList<>();

        resolveDirs.stream().forEach(z -> outList.add(Paths.get(z).toAbsolutePath().toString()));
        return outList;
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
        return get(resHome, resourceFileName);
    }

//    /**
//     * Extract the parameter values from the shell property
//     *
//     * @param shellProperties Properties object
//     * @param testConfig      Holds lists os parameters we need
//     * @return The test's parameters, if they exist in the properties
//     */
//    private static Hashtable<String, String> getTestArgs(final FilePropertyManager shellProperties, final AuditTestConfig testConfig) {
//        // extract the property values the test needs
//        Hashtable<String, String> propertyArgs = ResolveArgNames(testConfig.getArgNames(), shellProperties);
//
//        // Add global parameters
//        String errorsAsWarning = shellProperties.getPropertyString("ErrorsAsWarning");
//        if ((errorsAsWarning != null) && !errorsAsWarning.isEmpty())
//        {
//            propertyArgs.put("ErrorsAsWarning", errorsAsWarning);
//        }
//        return propertyArgs;
//    }
}
