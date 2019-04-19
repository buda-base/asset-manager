package io.bdrc.am.audit;

import io.bdrc.am.audit.iaudit.AuditTestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;

class AuditTestTestBase {

     protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    AuditTestTestBase() {

    }


    /**
     * Loads test dictionary from the resulting jar.
     * Returns classes which should be runnable
     *
     * @param jarUrl       the jar url
     * @param testDictName the test dict name
     * @return the hash table named by testDictName
     */


    @SuppressWarnings("unchecked")
    Hashtable<String, AuditTestConfig> getTestDictionary(URL jarUrl, @SuppressWarnings("SameParameterValue") String testDictName) {

        ClassLoader loader = URLClassLoader.newInstance(
                new URL[]{jarUrl},
                IsInstanceTest.class.getClassLoader());

        Hashtable<String, AuditTestConfig> result = null;

        Class testDict;
        try {
            testDict = Class.forName(testDictName, true, loader);
            Object instance = testDict.newInstance();
            Method method = testDict.getDeclaredMethod("getTestDictionary");

            result = (Hashtable<String, AuditTestConfig>) method.invoke(instance);
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            e.getMessage();
        }

        return result;
    }
}
