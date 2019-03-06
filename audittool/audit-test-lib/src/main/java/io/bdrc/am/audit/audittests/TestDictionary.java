package io.bdrc.am.audit.audittests;

import java.util.Hashtable;

/**
 * Moved from shell, so I can use class objects here, with names
 */
public class TestDictionary {

    public TestDictionary() {
        _TestDictionary = new Hashtable<String, Class>() {
            {
                put("FileSequence", FileSequence.class);
                put("NoFilesInFolder", NoFilesInRoot.class);
            }

        };
    }


    public Hashtable<String, Class> getTestDictionary() {
        return _TestDictionary;
    }

    private Hashtable<String, Class> _TestDictionary;

    // this is a placeholder for true dynamic linking:
    // See http://ronmamo.github.io/reflections/index.html?org/reflections/Reflections.html

    // private static Hashtable<String, Class> TestDictionary;

//    static {
//        TestDictionary = new Hashtable<String, Class>() {
//            {
//                put("FileSequence", FileSequence.class);
//                put("NoFilesInFolder", NoFilesInRoot.class);
//            }
//        };
//    }


}