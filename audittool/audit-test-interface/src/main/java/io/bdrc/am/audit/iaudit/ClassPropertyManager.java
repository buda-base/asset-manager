package io.bdrc.am.audit.iaudit;

import java.io.InputStream;

/**
 * Gets properties from a Java Class resource file
 */
public class ClassPropertyManager extends PropertyManager {


    public ClassPropertyManager(String resourcePath, Class clazz) {
        return PropertyManagerBuilder().MergeClassResource(resourcePath, clazz);
    }
}
