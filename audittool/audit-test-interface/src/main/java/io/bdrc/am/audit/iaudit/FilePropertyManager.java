package io.bdrc.am.audit.iaudit;

import java.io.*;

public class FilePropertyManager extends PropertyManager {

    @Override
    public InputStream LoadStream() throws IOException {
        FileInputStream fileInputStream;
        File external = new File(_resourcePath);
        if (!external.exists()) {
            throw new FileNotFoundException(_resourcePath);
        }
            fileInputStream = new FileInputStream(external);
        return fileInputStream;
    }

    public FilePropertyManager(String resourcePath) {
        super(resourcePath);
    }
}
