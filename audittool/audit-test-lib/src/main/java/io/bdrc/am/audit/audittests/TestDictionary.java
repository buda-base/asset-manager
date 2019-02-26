package io.bdrc.am.audit.audittests;

import java.util.Hashtable;

public class TestDictionary {

    public Hashtable<String, Class> getTestDictionary() {
        return _TestDictionary;
    }

    private Hashtable<String, Class> _TestDictionary = new Hashtable<String, Class>() {
        {
            put("FileSequence", FileSequence.class);
            put("NoFilesInFolder", NoFilesInRoot.class);
        }
    };
}

