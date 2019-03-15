package io.bdrc.am.audit.iaudit;

import java.io.InputStream;

public class ClassPropertyManager extends PropertyManager {
    @Override
    public InputStream LoadStream() {
        return _clazz.getResourceAsStream(_resourcePath);
    }

    private Class _clazz;

    public ClassPropertyManager(String resourcePath, Class clazz) {
        super(resourcePath);
        _clazz = clazz;
    }
}
