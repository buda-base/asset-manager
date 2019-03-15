package io.bdrc.am.audit.iaudit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilePropertyManager extends PropertyManager {

    @Override
    public InputStream LoadStream() throws IOException {
        FileInputStream fileInputStream = null;
        String cr = new java.io.File(".").getCanonicalPath();
        File external = new File(_resourcePath);
        if (external.exists()) {
            fileInputStream = new FileInputStream(external);
        }
        return fileInputStream;
    }

    public FilePropertyManager(String resourcePath) {
        super(resourcePath);
    }
}
