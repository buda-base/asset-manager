package io.bdrc.audit.audittests;

import io.bdrc.audit.iaudit.AuditTestConfig;
import io.bdrc.audit.iaudit.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

class AuditTestTestBase {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    protected final PropertyManager propertyManager;

    AuditTestTestBase() {

        // jimk asset-manager-158 - discovered that tests need a property manager
        // See audit-test-shell...shell.java for init sequence.

        propertyManager = PropertyManager.PropertyManagerBuilder();

        try {
            Path rp = Paths.get("src/test/resources/testResource.properties").toAbsolutePath();
            if (Files.exists(rp)) {
                propertyManager.MergeResourceFile(rp.toString(),"test base base class");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads test dictionary from the resulting jar.
     * Returns class which should be runnable
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

        Class<?> testDict;
        try {
            testDict = Class.forName(testDictName, true, loader);
            Object instance = testDict.getDeclaredConstructor().newInstance();
            Method method = testDict.getDeclaredMethod("getTestDictionary");

            result = (Hashtable<String, AuditTestConfig>) method.invoke(instance);
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            logger.error("Get Test Dictionary {}", e.getMessage(), e ) ;
        }

        return result;
    }
}
