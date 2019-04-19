package io.bdrc.am.audit.iaudit;


import java.util.Hashtable;

public class TestDictionary {

    public TestDictionary() {
        _TestDictionary = new Hashtable<>();
    }


    public Hashtable<String, AuditTestConfig> getTestDictionary() {
        return _TestDictionary;
    }

    final private Hashtable<String, AuditTestConfig> _TestDictionary;
}