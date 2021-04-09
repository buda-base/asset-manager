package io.bdrc.am.audit.iaudit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Property Manager Reads properties from a file, and allows merging
 * properties from other files. The values in any later merged file
 * will override the same values provided in an earlier file.
 * <p>
 * Caller is responsible for ordering the calls
 */
public class PropertyManager {

    // region constants


    // Same as in shell.properties UserConfigPath key
    private final String UserConfigPathKey = "UserConfigPath";

    // as in the config file, this is relative to user's home directory, if not an absolute path
    private final String UserConfigPathValue = Paths.get(".config", "bdrc", "auditTool", "config").toString();

    /**
     * Load Properties from a stream
     * @param resourceStream any kind of input stream - string, file, etc
     * @return self, with state change
     */
    public PropertyManager LoadProperties(InputStream resourceStream) {

        try {
            _Properties.load(resourceStream);
            if (logger.isDebugEnabled()) {
                DumpProperties();
            }
        } catch (IOException e) {
            logger.error("Could not load resource stream {}.", e.getMessage());
        }
        return _instance;
    }

    private InputStream InputFileResource(String filePath) throws IOException {
        try {
            String cr = new File(filePath).getCanonicalPath();
            File external = new File(cr);
            return new FileInputStream(external);
        } catch (IOException e) {
            logger.error("Couldn't open Input File Resource {} error {}", filePath, e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private InputStream InputJarResource(String path, Class<?> clazz) {
        return clazz.getResourceAsStream(path);
    }

    /**
     * Merges properties from the file specified in the default UserConfigPath (if any)
     * The user config file path is ALWAYS the value of the property stored in the field
     * UserConfigPathKey
     * If the property value of this key is an absolute path, use it,
     * otherwise the path is relative to system user.home (NOT user.dir)
     *
     * @return this same object, with state changed
     */
    public PropertyManager MergeUserConfig()  {

        String configPathValue = _Properties.getProperty(UserConfigPathKey);
        if (StringUtils.isBlank(configPathValue)) return this;

        Path configPath = Paths.get(configPathValue);
        if (!configPath.isAbsolute()) {
            // resolve with respect to user home
            String userHome = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
            configPath = Paths.get(userHome, configPathValue);
        }
        try {
            LoadProperties(InputFileResource(configPath.toString()));
        } catch (IOException e) {
            logger.error(String.format("Couldn't open %s ",configPathValue),e);
        }
        return this;
    }

    /**
     * Load an arbitrary set of properties. Most often used to load
     * the java system properties. See shell.java
     *
     * @return same object, with state changed
     */
    public PropertyManager MergeProperties(Properties externalProperties) {
        StringWriter sw = new StringWriter();
        try {
            externalProperties.store(sw, "system properties");
            StringReader sr = new StringReader(sw.toString());
            _Properties.load(sr);
            sw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Add a specific key to the properties
     *
     * @param key   resource id
     * @param value resource
     * @return instance
     */
    private PropertyManager MergeProperty(final String key, final String value) {
        if (logger.isDebugEnabled()) DumpProperties();
        _Properties.setProperty(key, value);
        if (logger.isDebugEnabled()) DumpProperties();
        return _instance;
    }

    /**
     * Merge File resource
     *
     * @param resourcePath Path on file stream
     * @return modified instance
     */
    public PropertyManager MergeResourceFile(String resourcePath) throws IOException {
        InputStream inputStream = InputFileResource(resourcePath);
        LoadProperties(inputStream);
        return this;
    }

    /**
     * Class in some other module load
     *
     * @param resourcePath relative to module
     * @param clazz        host class
     * @return modified this
     */
    public PropertyManager MergeClassResource(String resourcePath, Class<?> clazz) {
        InputStream ins = InputJarResource(resourcePath, clazz);
        LoadProperties(ins);
        return this;
    }

    /**
     * If you have a raw stream
     *
     * @param ins input stream
     * @return modified this
     */
    public PropertyManager MergeInputStream(InputStream ins) {
        LoadProperties(ins);
        return this;
    }

    private void DumpProperties() {
        logger.debug(String.format("Existing properties with length %d", _Properties.size()));
        _Properties.forEach((k, v) -> logger.debug("key :" + k + ":   value :" + v + ":"));
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
                put("io.bdrc.am.audit.audittests." + UserConfigPathKey, UserConfigPathValue);
            }
        };
    }

    /**
     * No public access. Use accessors by type (getPropertyInt, getPropertyString)
     */
    private Properties _Properties;

    /**
     * Read integer resource from in core dictionary
     *
     * @param key unique name
     * @return value of key from in-memory properties
     */
    public int getPropertyInt(String key)
    {
        if (logger.isDebugEnabled()) {
            DumpProperties();
        }

        String resourceValue = _Properties.getProperty(key);
        int rc;
        try {
            rc = Integer.parseInt(resourceValue);
        } catch (NumberFormatException e) {

            // asset-manager-106 - read from VM Args
            logger.error(String.format("Could not parse resource %s string value %s", key, resourceValue));
            throw e;
        }

        return rc;
    }

    /**
     * get a property's value and return as a string
     *
     * @param key property id
     * @return string value, or empty string if not found
     */
    public String getPropertyString(String key) {

        if (logger.isDebugEnabled()) {
            DumpProperties();
        }

        String rc = _Properties.getProperty(key);
        if (StringUtils.isEmpty(rc)) {
            rc = "";
        }

        return rc;
    }

    final private Logger logger;

    private static PropertyManager _instance;

    // region builder methods

    /**
     * Constructor - loads default properties from this class
     */
    private PropertyManager() {
        logger = LoggerFactory.getLogger(getClass());
        _Properties = BuildDefaultProperties();
    }

    /**
     * Builder pattern
     *
     * @return a PropertyManager instance with internal defaults loaded
     */
    public static PropertyManager PropertyManagerBuilder() {
        if (_instance == null) {
            _instance = new PropertyManager();
        }
        return _instance;
    }

    // endregion
}
