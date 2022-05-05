package io.bdrc.audit.shell;

import io.bdrc.audit.iaudit.*;
import io.bdrc.audit.iaudit.message.TestMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
@SuppressWarnings("PlaceholderCountMatchesArgumentCount")
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
    private static final String AT_VERSION = "auditToolVersion";
    private static final String TEST_LOGGER_HEADER = "id,test_name,outcome,error_number,error_test,detail_path\n";

    // should get thing2 whose name is io.bdrc.am.audit.shell.shell
    private static Logger sysLogger;
    private static Logger detailLogger;
    private static Logger testResultLogger;

    private final static int SYS_OK = 0;
    private final static int SYS_ERR = 1;

    private static AuditTestLogController testLogController;
    private final static String defaultPropertyFileName = "shell.properties";

    public static void main(String[] args) {
        List<Integer> allResults = new ArrayList<>();


        sysLogger = LoggerFactory.getLogger("sys"); // shellLogger.name=shellLogger //("root");
        detailLogger = LoggerFactory.getLogger("detailLogger"); //("root");
        testResultLogger = LoggerFactory.getLogger("testResultLogger");

        try {
            sysLogger.trace("Entering main");
            sysLogger.trace("Parsing args");
            ArgParser argParser = new ArgParser(args);

            if (!argParser.getParsed()) {
                sysLogger.trace("Invalid arguments");
                System.out.println("Exiting on Invalid arguments");
                System.exit(SYS_ERR);
            }

            sysLogger.trace("Resolving properties");
            Path resourceFile = resolveResourceFile();

            Properties cliProps = getCommandLineProperties(argParser);

            // TODO: try /shell.properties class

            // Property loading:
            // First, /shell.properties
            // Second, the user properties (defined in the /shell.properties property UserConfigPath)
            // UNLESS the userConfigPath was given in the command line
            // Third, the command line properties (defined by the -Dprop=value)
            // If the command line properties contains the UserConfigPath, such as
            // -D UserConfigPath=some_other/path the properties in that path

            PropertyManager shellProperties =
                    PropertyManager.PropertyManagerBuilder()
                            .MergeProperties(System.getProperties())
                            .MergeResourceFile(resourceFile.toAbsolutePath().toString());

            // Some special processing. If the command line overrides the default user properties file, set that
            // property now (the default resource file sets it above
            // Then, override it with other command line properties
            if (cliProps.containsKey(PropertyManager.UserConfigPathKey)) {
                shellProperties.PutProperty(PropertyManager.UserConfigPathKey,

                        // Ouch - can't put a Path object into the system environment
                        String.valueOf(PropertyManager.GetPathWithEnv(
                                cliProps.getProperty(PropertyManager.UserConfigPathKey))));
                cliProps.remove(PropertyManager.UserConfigPathKey);
            }

            // finally, merge the (possibly overridden)
            shellProperties = shellProperties.MergeUserConfig()
                    .MergeProperties(cliProps);

            // If we're only printing up info or syntax, just bail. We need to wait until
            // all properties have been resolved to be able to get the version
            if (argParser.OnlyShowInfo(shellProperties.getPropertyString(AT_VERSION))) {
                System.exit(SYS_OK);
            }



            // Replaced with class
            TestJarLoader testJarLoader = new TestJarLoader();

            String tdClassName = shellProperties.getPropertyString(TEST_DICT_PROPERTY_NAME);
            sysLogger.debug("{} value of property :{}:", TEST_DICT_PROPERTY_NAME, tdClassName);
            Hashtable<String, AuditTestConfig> testDictionary = testJarLoader.LoadDictionaryFromProperty("testJar", tdClassName);

            assert testDictionary != null;

            // jimk asset-manager-165 - query test names
            if (argParser.HasOnlyShowTestNames()) {
                testDictionary.forEach((k, v) -> System.out.printf("%-30s\t%s\n", v.getKey(), v.getFullName()));
                System.exit(SYS_OK);
            }

            // jimk asset-manager-165 Add test names
            Hashtable<String, AuditTestConfig> requestedTests = FilterTestNamesByRequested(testDictionary, argParser.getRequestedTests());

            testLogController = BuildTestLog(argParser);

            if (argParser.has_DirList()) {
                for (String aTestDir : ResolvePaths(argParser.getDirs())) {
                    sysLogger.debug("arg =  {} ", aTestDir);
                    allResults.addAll(RunTestsOnDir(shellProperties, requestedTests, aTestDir));
                }
            }

            // don't force mutually exclusive. Why not do both?
            if (argParser.getReadStdIn()) {
                String curLine;
                try (BufferedReader f = new BufferedReader(new InputStreamReader(System.in))) {
                    while (null != (curLine = f.readLine())) {
                        sysLogger.debug("readLoop got line {} ", curLine);
                        allResults.addAll(RunTestsOnDir(shellProperties, requestedTests, curLine));
                    }
                }
            }

        } catch (Exception e) {
            System.out.printf("Exiting on exception %s\n", e.getMessage());
            sysLogger.error(e.toString(), e, "Exiting on Exception", "Fail");
            System.exit(SYS_ERR);
        }

        Stream<Integer> rs = allResults.stream();
        boolean anyFailed = rs.anyMatch(x -> x.equals(Outcome.FAIL));
        sysLogger.trace("Exiting any failed {}", anyFailed);
        System.exit(anyFailed ? SYS_ERR : SYS_OK);
    }

    /**
     * Removes all test names from test dictionary except those that are requested.
     * logs a warning for tests which are requested but not present
     *
     * @param testDictionary Input: Complete list of tests in the test jar
     * @param requestedTests list of desired tests
     * @return reduced testDictionary
     */
    private static Hashtable<String, AuditTestConfig> FilterTestNamesByRequested(Hashtable<String, AuditTestConfig> testDictionary,
    final List<String> requestedTests)
    {

        Hashtable<String, AuditTestConfig> results =new Hashtable<>(testDictionary.size());

        if (requestedTests == null || requestedTests.isEmpty()) {
            return testDictionary;
        }

        List<Map.Entry<String, AuditTestConfig>> requested =
                testDictionary
                        .entrySet()
                        .stream()
                        .filter(x -> requestedTests.contains(x.getKey()))
                        .toList();


        requested.forEach(r -> results.put(r.getKey(), r.getValue()));

        requestedTests.forEach(r -> {
            if (!results.containsKey(r)) {
                sysLogger.warn("Requested test {} not found in test library", r);
            }
        });
        return results;


    }

    private static Properties getCommandLineProperties(final ArgParser argParser) {
        // Merge properties given on the command line:
        List<String> cliOptions = argParser.getDefinedOptions();
        Properties cliProperties = new Properties();
        if (cliOptions.size() > 0) {
            cliOptions.forEach(
                    optArg -> {
                        String[] optArgKV = optArg.split("=");
                        if (optArgKV.length == 2) {
                            cliProperties.put(optArgKV[0], optArgKV[1]);
                        } else {
                            sysLogger.warn(" Invalid option format {}", optArg);
                        }
                    });
        }
        return cliProperties;
    }

    private static AuditTestLogController BuildTestLog(final ArgParser ap)
    {
        AuditTestLogController tlc;
        String logDir = ap.getLogDirectory();
        tlc = new AuditTestLogController();
        tlc.setCsvHeader(shell.TEST_LOGGER_HEADER);
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
    private static List<Integer> RunTestsOnDir(final PropertyManager shellProperties,
                                               final Hashtable<String, AuditTestConfig> testSet, final String aTestDir)
            throws IOException
    {

        // testLogController ctor sets test log folder
        testLogController.ChangeAppender(BuildTestLogFileName(aTestDir));

        List<TestResult> results = new ArrayList<>();
        for (String testName : testSet.keySet()) {

            AuditTestConfig testConfig = testSet.get(testName);

            // Do we have a value at all?
            if (testConfig == null) {

                // sysLogger goes to a csv and a log file, so add the extra parameters.
                // log4j won't care.
                String errorStr = String.format("No test config found for %s. Contact library provider.", testName);
                sysLogger.error(errorStr, errorStr, "Failed");
                results.add(new TestResult(Outcome.FAIL, errorStr));
                continue;
            }

            // Is this test an  IAuditTest?
            Class<?> testClass = testConfig.getTestClass();
            if (!IAuditTest.class.isAssignableFrom(testClass)) {
                String errorStr = String.format("Test found for %s does not  implement IAudit", testName);
                sysLogger.error(errorStr, errorStr, "Failed");
                results.add(new TestResult(Outcome.FAIL, errorStr));
                continue;
            }

            // sysLogger always runs logger to console, this logs it to file
            Logger testLogger = LoggerFactory.getLogger(testClass);

            // descriptive
            String testDesc = testConfig.getFullName();

            // extract the property values the test needs
            Hashtable<String, String> propertyArgs = ResolveArgNames(testConfig.getArgNames(), shellProperties);


            results.add(TestOnDirPassed((Class<IAuditTest>) testClass, testLogger, testName, testDesc, propertyArgs,
                    aTestDir));
        }

        if (results.stream().allMatch(TestResult::Passed)) testLogController.RenameLogPass();
        else if (results.stream().anyMatch(TestResult::Failed)) testLogController.RenameLogFail();
        else if (results.stream().anyMatch(TestResult::Skipped)) testLogController.RenameLogWarn();
        else if (results.stream().anyMatch(TestResult::Warnings)) testLogController.RenameLogWarn();

        // return the outcomes. Don't distinct, someone may want to do stats
        return results.stream().map(TestResult::getOutcome)
                .collect(Collectors.toList());
    }

    /**
     * Create the file out of a parameter and the date, formatted yyyy-mm-dd-hh-mm
     * Date time pattern should match the formats in log4j2.properties
     *
     * @param aTestDir full name of folder
     */
    private static String BuildTestLogFileName(final String aTestDir) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("-yyyy-MM-dd-kk-mm")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());

        String fileDate = dtf.format(Instant.now());
        return Paths.get(aTestDir).getFileName().toString() + fileDate + ".csv";
    }

    private static TestResult TestOnDirPassed(final Class<IAuditTest> testClass,
                                              final Logger testLogger,
                                              final String testName,
                                              final String testDesc,
                                              final Hashtable<String, String> propertyArgs,
                                              final String testDir)
    {
        sysLogger.debug("Invoking {}. Params :{}:", testDesc, testDir);

        TestResult tr = null;
        try {
            tr = RunTest(testLogger, testName, testClass, testDir, propertyArgs);

            // String resultLogFormat = "Result:%10s\tFolder:%20s\tTest:%30s";
            String resultLogFormat = "{}\t{}\t\t{}";

            String workName = Paths.get(testDir).getFileName().toString();

            String testResultLabel;

            Integer outcome = tr.getOutcome();
            if (outcome.equals(Outcome.SYS_EXC) || outcome.equals(Outcome.FAIL)) {
                testResultLabel = "Failed";
                sysLogger.error(resultLogFormat, testResultLabel, testDir, testDesc);
            } else if (outcome.equals(Outcome.PASS)) {
                testResultLabel = "Passed";
                sysLogger.info(resultLogFormat, testResultLabel, testDir, testDesc);
            } else if (outcome.equals(Outcome.NOT_RUN)) {
                testResultLabel = "Not Run";
                sysLogger.warn(resultLogFormat, testResultLabel, testDir, testDesc);
            } else if (outcome.equals(Outcome.WARN)) {
                testResultLabel = "Warnings";
                sysLogger.warn(resultLogFormat, testResultLabel, testDir, testDesc);
            } else {
                testResultLabel = String.format("Unknown result status %d", tr.getOutcome());
                sysLogger.error(resultLogFormat, testResultLabel, testDir, testDesc);
            }

            // Test result logger doesn't have levels
            // See testLogController.setSVCFormat above. Provide params for all
            // headings. In CSV format, first arg is ignored.
            //"id,test_name,outcome,detail_path,error_number,error_test"
            testResultLogger.info("ignoredCSV", workName, testDesc, testResultLabel, null, null, testDir);
            String errorFormat = "{}:{}:{}";

            for (TestMessage tm : tr.getErrors()) {
                if (outcome.equals(Outcome.NOT_RUN)) {
                    detailLogger.warn(errorFormat, tm.getOutcome().toString(), tm.getMessage(), testDir);
                } else if (outcome.equals(Outcome.PASS)) {
                    detailLogger.info(errorFormat, tm.getOutcome().toString(), tm.getMessage(), testDir);
                } else {
                    detailLogger.error(errorFormat, tm.getOutcome().toString(), tm.getMessage(), testDir);
                }

                // We don't repeat the first few columns for detailed errors
                // testResultLogger also has no level.
                testResultLogger.info("ignoredCSV", null, null, null, tm.getOutcome().toString(), tm.getMessage(),
                        testDir);
            }
        } catch (Exception e) {
            System.out.printf("%s %s\n", testDir, testClass.getCanonicalName());
            e.printStackTrace();
        }

        return tr;
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
        if ((errorsAsWarning != null) && !errorsAsWarning.isEmpty()) {
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

        resolveDirs.forEach(z -> outList.add(Paths.get(z).toAbsolutePath().toString()));
        return outList;
    }

    /**
     * RunTest
     * Shell to run a test instance, given its class
     *
     * @param testLogger Logger for the test. Not the same as the shell logger
     * @param params     array of additional parameters. Caller has to prepare it for each test. (Needs more structure)
     */
    private static TestResult RunTest(Logger testLogger, final String testName,
                                      Class<IAuditTest> testClass, Object... params)
    {

        String className = testClass.getCanonicalName();

        TestResult tr = new TestResult();
        try {
            // All instances are required to have a Logger, String constructor for the shell
            // Unit tests don't always use it.
            Constructor<IAuditTest> ctor = testClass.getConstructor(Logger.class, String.class);
            IAuditTest inst = ctor.newInstance(testLogger, testName);

            inst.setParams(params);
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
    private static Path resolveResourceFile() {

        String resHome = System.getProperty("atHome");

        if (StringUtils.isEmpty(resHome)) {
            sysLogger.debug("resolveResourceFile: atHome empty.");
            resHome = System.getenv("ATHOME");
            sysLogger.debug("resolveResourceFile: getenv ATHOME {}", resHome);
        }

        // asset-manager-34. Need to get the directory the jar is running
        // in if there is no atHome (e.g. if debugging)
        if (StringUtils.isEmpty(resHome)) {
            try {
                resHome = shell.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
                        .getPath();
                sysLogger.debug("path from class: {}", resHome);
            } catch (URISyntaxException e) {
                sysLogger.debug("Invalid URI from java URL {}", e);
                // don care, just keep going
            }

            // if all else fails
            if (StringUtils.isEmpty(resHome)) {
                resHome = System.getProperty("user.dir");
                sysLogger.debug("resolveResourceFile: getenv user.dir {}", resHome);
            }

        }

        sysLogger.debug("Resource home is {} ", resHome, " is resource home path");
        return Paths.get(resHome, shell.defaultPropertyFileName);
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
