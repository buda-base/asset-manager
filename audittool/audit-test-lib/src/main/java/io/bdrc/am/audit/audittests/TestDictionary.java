package io.bdrc.am.audit.audittests;

import io.bdrc.am.audit.iaudit.AuditTestConfig;

import java.util.Arrays;
import java.util.Hashtable;

/**
 * Moved from shell, so I can use class objects here, with names
 * placeholder for true dynamic linking:
 * See http://ronmamo.github.io/reflections/index.html?org/reflections/Reflections.html
 *
 */
@SuppressWarnings("unused")
public class TestDictionary {

    public TestDictionary() {
    }


    public Hashtable<String, AuditTestConfig> getTestDictionary() {
        return _TestDictionary;
    }

    private final Hashtable<String, AuditTestConfig> _TestDictionary = new Hashtable<String, AuditTestConfig>() {
        {
            put("FileSequence", new AuditTestConfig("File Sequence Test",
                    Arrays.asList(
                            "ArchiveGroupParent", "DerivedImageGroupParent"),
                    "FileSequence", FileSequence.class));

            //noinspection ArraysAsListWithZeroOrOneArgument
            put("NoFilesInFolder", new AuditTestConfig("No Files in Root Folder",
                    Arrays.asList(""),
                    "NoFilesInFolder",
                    NoFilesInRoot.class));
        }
    };

}