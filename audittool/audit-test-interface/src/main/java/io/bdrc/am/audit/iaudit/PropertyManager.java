package io.bdrc.am.audit.iaudit;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropertyManager {

    public abstract InputStream LoadStream() throws IOException;

    //  region property resources

    /**
     * Load all properties
     */
    private void LoadProperties() {
        if (_Properties != null) {
            logger.debug(String.format("Existing properties with length %d", _Properties.size()));
            _Properties.forEach((k, v) -> logger.debug("key :" + k + ":   value :" + v + ":"));
            return;
        }
        logger.debug("Load Properties");
        try {
            // hard-wired name of system property resource file

            _Properties = new Properties(BuildDefaultProperties());

            InputStream props = LoadStream();
            if (props == null) {
                logger.error("Cant open resource {}", _resourcePath);
            } else {
                _Properties.load(props);
                props.close();
            }

        } catch (Exception e) {
            logger.error("Caught exception, setting properties to defaults", e);
            //  _Properties = BuildDefaultProperties();
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
                put("io.bdrc.am.audit.audittests.FileSequence.SequenceLength", "4");
            }
        };
    }

    /**
     * No public access. Use accessors by type (getPropertyInt, getPropertyString)
     */
    private Properties _Properties;

    String _resourcePath;

    /**
     * Read integer resource from in core dictionary
     *
     * @param resourceName unique name
     * @return value of resourceName from in-memory properties
     */
    public int getPropertyInt(String resourceName) {

        // LoadProperties is cheap to recall
        LoadProperties();
        _Properties.forEach((k, v) -> logger.debug("key :{}: value :{}:", k, _Properties.getProperty(k.toString())));

        String resourceValue = _Properties.getProperty(resourceName);
        int rc;
        try {
            rc = Integer.parseInt(resourceValue);
        } catch (Exception e) {
            logger.error(String.format("Could not parse resource %s string value %s", resourceName, resourceValue));
            throw e;
        }

        return rc;
    }

    public String getPropertyString(String resourceName) {

        LoadProperties();
        String rc = _Properties.getProperty(resourceName);
        if (StringUtils.isEmpty(rc)) {
            rc = "";
        }

        return rc;
    }

    private Logger logger;

    public PropertyManager(String resourcePath) {
        logger = LoggerFactory.getLogger(getClass());
        _resourcePath = resourcePath;
        _Properties = null;
    }


    // endregion
}
