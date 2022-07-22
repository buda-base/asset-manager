package io.bdrc.audit.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * Container for logging utilities.
 */
public class CoreLogger {
    public static Logger getCoreLogger(org.slf4j.Logger logger) {
        if (logger == null) {
            return null;
        }
        return (org.apache.logging.log4j.core.Logger) LogManager.getLogger(logger.getName());
    }
}
