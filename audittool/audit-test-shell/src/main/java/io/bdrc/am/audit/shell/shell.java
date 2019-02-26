package io.bdrc.am.audit.shell;

import io.bdrc.am.audit.audittests.TestDictionary;
import io.bdrc.am.audit.iaudit.IAuditTest;
import io.bdrc.am.audit.iaudit.Outcome;
import io.bdrc.am.audit.iaudit.TestMessage;
import io.bdrc.am.audit.iaudit.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;


public class shell {

    // this is a placeholder for true dynamic linking:
    // See http://ronmamo.github.io/reflections/index.html?org/reflections/Reflections.html
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    // private static Hashtable<String, Class> TestDictionary;

//    static {
//        TestDictionary = new Hashtable<String, Class>() {
//            {
//                put("FileSequence", FileSequence.class);
//                put("NoFilesInFolder", NoFilesInRoot.class);
//            }
//        };
//    }


/*
  Call with an implementation of audit test library in the
  classpath
  @param args  See usage
  */
    public static void main(String[] args) {


        Hashtable<String, Class> td = (new TestDictionary()).getTestDictionary();

        // Get the jar to examine
        ArrayList<String> dirsToTest = (new ArgParser(args)).getDirs();

        Logger sysLogger = LoggerFactory.getLogger("sys");
        for (String testName : td.keySet()) {

            Logger testLogger = LoggerFactory.getLogger(td.get(testName));

            ResolvePaths(dirsToTest);

            // Cant use Object[] directly
            String[] runArgs = new String[dirsToTest.size()];
           dirsToTest.toArray(runArgs);

           for (String aTestDir : dirsToTest) {
               sysLogger.info("Test {} invoked. Params :{}:", testName, aTestDir);
               TestResult tr = RunTest(testLogger, td.get(testName), aTestDir);

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

    /**
     * In place replacement of paths with their resolved value
     * @param resolveDirs list of paths to resolve
     */
    private static void ResolvePaths(final ArrayList<String> resolveDirs) {
        for (int i= 0 ; i < resolveDirs.size(); i++) {
            resolveDirs.set(i, Paths.get(resolveDirs.get(i)).toAbsolutePath().toString());
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
