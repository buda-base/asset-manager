package io.bdrc.am.audit.iaudit;

import java.util.Hashtable;

public interface ITestDictionary {
    Hashtable<String, AuditTestConfig> getTestDictionary() ;
}