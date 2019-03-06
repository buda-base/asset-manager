package io.bdrc.am.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;

class AuditTestTestBase {

    Logger logger = LoggerFactory.getLogger(this.getClass());

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
    Hashtable<String, Class> getTestDictionary(URL jarUrl, String testDictName) {


        ClassLoader loader = URLClassLoader.newInstance(
                new URL[]{jarUrl},
                IsInstanceTest.class.getClassLoader());

        Hashtable<String, Class> result = null;


        Class testDict;
        try {
            testDict = Class.forName(testDictName, true, loader);
            Object instance = testDict.newInstance();
            Method method = testDict.getDeclaredMethod("getTestDictionary");
            result = (Hashtable<String, Class>) method.invoke(instance);
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return result;
    }
}
