package io.bdrc.am.audit.iaudit;

import org.slf4j.Logger;

public interface IAuditTest {

    void LaunchTest();
    String getTestName();
    TestResult getTestResult();
    void setParams(Object...params);
    void setLogger(Logger logger);
}
