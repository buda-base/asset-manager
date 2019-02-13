package io.bdrc.am.audit.shell;

import io.bdrc.am.audit.audittests.*;

import io.bdrc.am.audit.iaudit.IAuditTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Hashtable;
import io.bdrc.am.audit.iaudit.Outcome;


public class shell {

    // this is a placeholder for true dynamic linking:
    // See http://ronmamo.github.io/reflections/index.html?org/reflections/Reflections.html
    private static Hashtable<String, Class> TestDictionary ;

    static {
        TestDictionary = new Hashtable<String, Class>() {
            {
                put("FileSequence",  FileSequence.class);
                put("NoFilesInFolder", NoFilesInRoot.class);
            }
        };
    }
    public static void main(String[] args) throws MalformedURLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        Logger logger = LoggerFactory.getLogger("main");
//        Logger t1 = LoggerFactory.getLogger("thing1");
//        Logger t2 = LoggerFactory.getLogger("thing2");
//
//        logger.info("Greetings from Main");
//        t1.info("Et tu thing1");
//        t2.info("Et tu thing2");

        // Get the jar to examine
        if (null == args) {
            for ( String testName : TestDictionary.keySet()) {

               Logger testLogger = LoggerFactory.getLogger(TestDictionary.get(testName));

               Path here = Paths.get("");

               RunTest(testLogger,TestDictionary.get(testName), here.toString());

            }
        }

//
//        File file  = new File(args[0]);
//
//        URL url = file.toURI().toURL() ;
//        URL[] urls = new URL[]{url};
//
//        ClassLoader cl =  URLClassLoader.newInstance(urls);
//
//        Package
//
//        for (Class clz : cl.)
//        Class cls = cl.loadClass("com.mypackage.myclass");


    }

    private static void RunTest(Logger logger, Class testClass, String ... params) throws NoSuchMethodException,
            IllegalAccessException
            , InvocationTargetException, InstantiationException {

        String className = testClass.getClass().getCanonicalName();

        try {
            Constructor ctor = testClass.getConstructor(Logger.class);
            IAuditTest inst = (IAuditTest) ctor.newInstance(logger);

            try {
                inst.setParams(params);
                inst.LaunchTest();
            } catch (Exception eek) {
                logger.error(" Exception {} when running test {}", eek, className);
            }
        }
        catch (Exception eek) {
            logger.error("Could not create or invoke {}. Exception {} ",className), eek);
        }
    }
}
