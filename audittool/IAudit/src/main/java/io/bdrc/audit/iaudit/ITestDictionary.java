package io.bdrc.audit.iaudit;

import java.util.Hashtable;

public interface ITestDictionary {
    Hashtable<String, AuditTestConfig> getTestDictionary() ;
}