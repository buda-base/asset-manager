package io.bdrc.am.audit.iaudit;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public class TestFactory {
    public static TestDictionary LoadTests(URI library) throws MalformedURLException {
        classLoader(library.toURL());
        return new TestDictionary();
    }

    static void classLoader(URL libUrl) {
        ClassLoader loader = URLClassLoader.newInstance(
                new URL[] { libUrl },
                TestDictionary.class.getClassLoader()
        );



        // the samples want to pull a class by name, but I might switch over to reflection here
        // https://github.com/ronmamo/reflections

//        Class<?> clazz = Class.forName("mypackage.MyClass", true, loader);
//        Class<? extends Runnable> runClass = clazz.asSubclass(Runnable.class);
//// Avoid Class.newInstance, for it is evil.
//        Constructor<? extends Runnable> ctor = runClass.getConstructor();
//        Runnable doRun = ctor.newInstance();
//        doRun.run();
    }
}
