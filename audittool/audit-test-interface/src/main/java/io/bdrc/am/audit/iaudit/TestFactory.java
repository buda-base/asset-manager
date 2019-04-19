package io.bdrc.am.audit.iaudit;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

class TestFactory {
    static TestDictionary LoadTests(URI library) throws MalformedURLException {
        classLoader(library.toURL());
        return new TestDictionary();
    }

    private static void classLoader(URL libUrl) {
        ClassLoader loader = URLClassLoader.newInstance(
                new URL[] { libUrl },
                TestDictionary.class.getClassLoader()
        );
    }
}
