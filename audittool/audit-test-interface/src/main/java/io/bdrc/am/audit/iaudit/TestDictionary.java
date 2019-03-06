package io.bdrc.am.audit.iaudit;


import java.util.Hashtable;

public class TestDictionary {

    public TestDictionary() {
        _TestDictionary = new Hashtable<String,Class>();
    }


    public Hashtable<String, Class> getTestDictionary() {
        return _TestDictionary;
    }

    private Hashtable<String, Class> _TestDictionary ;


}