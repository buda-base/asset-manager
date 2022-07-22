package io.bdrc.audit.log;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import java.nio.file.Paths;

/**
 * Set up all logs
 */

public class AuditLogController {
    // rewrite of AuditLogController0, using
    // the strategy of setting a system property before loading the log4j 2 configuration
    // https://newbedev.com/log4j2-assigning-file-appender-filename-at-runtime


    // WARNING:  MUST MATCH VALUE IN LOG4J2.PROPERTIES
    private static final String AT_LOGDIR_PROP_NAME = "AUDIT_LOG_ROOT" ;

    // Fallback if log dir is not set
    private static final String AT_LOGDIR_DEFAULT_PROP = "user.home" ;
    private static final String AT_LOGDIR_DEFAULT = "audit-test-logs";

    public static void setLogDirectory( String logDirectory) {

        String defaultLogDir = Paths.get(System.getProperty(AT_LOGDIR_DEFAULT_PROP),AT_LOGDIR_DEFAULT ).toString();
        logDirectory = StringUtils.isEmpty(logDirectory) ? defaultLogDir : logDirectory ;
        System.setProperty(AT_LOGDIR_PROP_NAME, logDirectory);

        // and reconfigure the logger:
        org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext)
                LogManager.getContext(false);
        ctx.reconfigure();
    }
}
