package io.bdrc.am.audit.audittests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {

    //  region property resources

    /**
     * Load all properties
     */
    void LoadProperties() {
        if (_AuditTestLibProperties != null) {
            logger.debug(String.format("Existing properties with length %d", _AuditTestLibProperties.size()));
            _AuditTestLibProperties.forEach((k, v) -> logger.debug("key :" + k + ":   value :" + v + ":"));
            return;
        }
        logger.debug("Load Properties");
        try {
            // hard-wired name of system property resource file

            InputStream props = PropertyManager.class.getResourceAsStream(_resourcePath);
            if (props == null) {
                throw new Exception("Cant open resource " + _resourcePath);
            }

            _AuditTestLibProperties = new Properties();
            _AuditTestLibProperties.load(props);

        } catch (Exception e) {
            logger.error("Caught exception, setting properties to defaults", e);
            _AuditTestLibProperties = BuildDefaultProperties();
        }
    }

    /**
     * Create hard wired properties
     *
     * @return new default properties
     */
    private Properties BuildDefaultProperties() {
        return new Properties() {
            {
                put("io.bdrc.am.audittests.FileSequence.SequenceLength", "4");
            }
        };
    }

    /**
     * No public access. Use accessors by type (getPropertyInt, getPropertyString)
     */
    private Properties _AuditTestLibProperties = null;

    private String _resourcePath;

    /**
     * Read integer resource from in core dictionary
     *
     * @param resourceName unique name
     * @return value of resourceName from in-memory properties
     */
    public int getPropertyInt(String resourceName) {

        // LoadProperties is cheap to recall
        LoadProperties();

        String resourceValue = _AuditTestLibProperties.getProperty(resourceName);
        int rc;
        try {
            rc = Integer.parseInt(resourceValue);
        } catch (Exception e) {
            logger.error(String.format("Could not parse resource %s string value %s", resourceName, resourceValue));
            throw e;
        }

        return rc;
    }

    private Logger logger;

    public PropertyManager(String resourcePath) {
        logger = LogManager.getLogger(getClass().getCanonicalName());
        _resourcePath = resourcePath;
    }


    // endregion
}
