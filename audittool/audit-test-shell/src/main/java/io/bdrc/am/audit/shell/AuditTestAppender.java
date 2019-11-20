package io.bdrc.am.audit.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;

import java.util.Enumeration;
import java.util.Map;

/**
 * Creates a new appender for a specific work
 */
public class AuditTestAppender {

    private static final String EACHWORKAPPENDER = "PerWorkAppender";
    /*
     *  Operations:
     *  - get and save the root logger
     *  - Create the file appender
     *  - if there's an existing appender, remove it.
     *  - Add the appender
     */
    // https://github.com/openmrs/openmrs-core/blob/master/api/src/main/java/org/openmrs/util/OpenmrsUtil.java#L505

    /**
     * @param appenderRootName Prefix for file name
     */
    public AuditTestAppender(String appenderRootName) {
        org.apache.logging.log4j.core.Logger rootLogger = (org.apache.logging.log4j.core.Logger)
                                                                  LogManager.getRootLogger();
        synchronized (rootLogger);
        FileAppender fileAppender = null;

        Map<String, Appender> existingAppenders =  rootLogger.getAppenders();

        Appender workAppender = BuildAppender();
        if (existingAppenders.containsKey(EACHWORKAPPENDER))
            thisAppender = existingAppenders.nextElement();
        }


    }

}
