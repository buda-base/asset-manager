package io.bdrc.am.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements system and individual test loggers. A client passes in
 * a reference to any class object. That object determines which test logger
 * is logged. The test logger file and format are fixed by the requirements
 * of some downstream consumer of the test results.
 *
 * The system logger has no constraints - can be anything developers feel useful.
 */
public class AuditLoggers {
    private Logger _testLogger = null;
    private Logger _sysLogger = LoggerFactory.getLogger(this.getClass());

    /**
     * Create new logger
     *
     * @param caller Class object of client requesting log services
     */
    public AuditLoggers(Class caller) {
        _testLogger = LoggerFactory.getLogger(caller);
    }
}
