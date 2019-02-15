package io.bdrc.am.audit.shell;

import io.bdrc.am.audit.audittests.FileSequence;
import io.bdrc.am.audit.audittests.NoFilesInRoot;
import io.bdrc.am.audit.iaudit.IAuditTest;
import io.bdrc.am.audit.iaudit.Outcome;
import io.bdrc.am.audit.iaudit.TestMessage;
import io.bdrc.am.audit.iaudit.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;


public class shell {

    // this is a placeholder for true dynamic linking:
    // See http://ronmamo.github.io/reflections/index.html?org/reflections/Reflections.html
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static Hashtable<String, Class> TestDictionary;

    static {
        TestDictionary = new Hashtable<String, Class>() {
            {
                put("FileSequence", FileSequence.class);
                put("NoFilesInFolder", NoFilesInRoot.class);
            }
        };
    }

    public static void main(String[] args) {

        // Get the jar to examine
        ArrayList<String> testArgs = (new ArgParser(args)).getArgs();

        Logger sysLogger = LoggerFactory.getLogger("sys");
        for (String testName : TestDictionary.keySet()) {

            Logger testLogger = LoggerFactory.getLogger(TestDictionary.get(testName));


            // use working dir as argument if none given
            if (testArgs.size() == 0) {
                testArgs.add(Paths.get("").toAbsolutePath().toString());
            }

            // Cant use Object[] directly
            String[] runArgs = new String[testArgs.size()];
            runArgs = testArgs.toArray(runArgs);

            StringBuilder argString = new StringBuilder();
            for ( String arg : runArgs) {
                argString.append(arg);
            }
            sysLogger.info("Test {} invoked. Parms {}", testName, argString.toString() );
            TestResult tr = RunTest(testLogger, TestDictionary.get(testName), runArgs);

            for (TestMessage tm : tr.getErrors()) {
                sysLogger.info("> Outcome: {}: {}",tm.getOutcome().toString(), tm.getMessage());
            }
            sysLogger.info("Test {} result {}", testName, tr.Passed()? "Passed": "Failed");

        }
    }

    /**
     * Shell to run a test instance, given its class
     *
     * @param testLogger Logger for the test. Not the same as the shell logger
     * @param params     parameters to the
     */
    private static TestResult RunTest(Logger testLogger, Class testClass, String... params) {

        String className = testClass.getCanonicalName();

        TestResult tr = new TestResult();
        try {
            @SuppressWarnings("unchecked")
            Constructor ctor = testClass.getConstructor(Logger.class);
            IAuditTest inst = (IAuditTest) ctor.newInstance(testLogger);

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
