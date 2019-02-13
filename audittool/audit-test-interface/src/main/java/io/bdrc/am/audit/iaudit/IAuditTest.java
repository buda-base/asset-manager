package io.bdrc.am.audit.iaudit;

public interface IAuditTest {

    void LaunchTest();
    String getTestName();
    TestResult getTestResult();
    void SetParams(Object...params);
}
