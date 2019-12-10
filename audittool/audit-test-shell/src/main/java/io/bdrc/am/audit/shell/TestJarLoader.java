package io.bdrc.am.audit.shell;

import io.bdrc.am.audit.iaudit.AuditTestConfig;

import io.bdrc.am.audit.iaudit.IAuditTest;
import io.bdrc.am.audit.iaudit.TestDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;

public class TestJarLoader {

    private Logger sysLogger = LoggerFactory.getLogger(this.getClass());


    /**
     * Fetches a test dictionary from a classpath bound library
     * @return a hashtable of test names and proxy classes
     */
    Hashtable<String, AuditTestConfig> LoadTestDictionary()
    {
        TestDictionary td = new TestDictionary();
        return td.getTestDictionary();
    }


    @SuppressWarnings({"unchecked"})
    Hashtable<String, AuditTestConfig> LoadDictionaryFromProperty(final String testJarSystemPropertyName,
                                                                                 String testDictClassPropertyName)
            throws
            Exception
    {
        String loc = "LoadDictionary";

        sysLogger.trace( "entering {}",loc);
        String jarPath = System.getProperty(testJarSystemPropertyName);
        if (jarPath == null)
        {
            String message = String.format("%s property not found", testJarSystemPropertyName);
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



        try
        {
            if (loader != null)
            {

                Class<IAuditTest> testDict = (Class<IAuditTest>) Class.forName(testDictClassPropertyName, true, loader); //, loader);
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
}
