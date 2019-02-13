package io.bdrc.am.audit;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class BuildTestData   {

    /**
     * Create test data
     *
     * @param rootId: opaque token to identify path
     */
    public BuildTestData(String rootId)
    {
        // Create a standard doc structure
        // Create a folder
        // Create subfolders
    }

    public void CreateWork()
    {

    }

    public Path getRootPath() {
        return _rootFile;
    }

    public void setRootFile(Path value) {
        _rootFile = value;
    }

    Path _rootFile;
}
