package io.bdrc.audit.iaudit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
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
    public final static String UserConfigPathKey = "UserConfigPath";

    // as in the config file, this is relative to user's home directory, if not an absolute path
    public final static String UserConfigPathValue =
            Paths.get(".config", "bdrc", "auditTool", "user.properties").toString();

    /**
     * Default env var for many files
     */
    public final static String UserHomeEnv = "user.home";

    /**
     * Load Properties from a stream
     *
     * @param resourceStream any kind of input stream - string, file, etc
     * @return self, with state change
     */
    public PropertyManager LoadProperties(InputStream resourceStream) {

        if (resourceStream == null) return _instance;
        try {
            _Properties.load(resourceStream);
            DumpProperties("Loading from input stream");
        } catch (IOException e) {
            logger.error("Could not load resource stream {}.", e.getMessage());
        }
        return _instance;
    }

    private InputStream InputFileResource(String filePath) throws IOException {
        try {

            // Have to translate to absolute path here. On Windows, /tmp/bladdbla is not
            // absolute, but on Linux it is.
            String cr = new File(filePath).getCanonicalPath();
            File external = new File(cr);
            return new FileInputStream(external);
        } catch (IOException e) {
            logger.warn("Couldn't open Input File Resource {} error {}", filePath, e.getMessage());
        }
        return null;
    }

    private InputStream InputJarResource(String path, Class<?> clazz) {
        return clazz.getResourceAsStream(path);
    }

    /**
     * Merges properties from the file specified in the property given by
     * the parameter 'configKey
     * If the property value of this key is an absolute path, use it,
     * otherwise the path is relative to system user.home (NOT user.dir)
     *
     * @return this same object, with state changed
     */

    public PropertyManager MergeUserConfig()
    {
        Path configPath = PathObjectFromProperty(UserConfigPathKey, UserHomeEnv);
        if (configPath == null) {
            return this;
        }
        logger.trace("> Merging user config from property key {} {}", UserConfigPathKey, configPath);
        return MergeConfigGivenInProperty(configPath);
    }

    /**
     * Merges properties from a file
     *
     * @param configPath existing file containing properties
     * @return self
     */
    public PropertyManager MergeConfigGivenInProperty(Path configPath) {

        logger.trace("Loading from Input resource {} ", configPath);
        try(InputStream ins = InputFileResource(configPath.toString())) {
            return LoadProperties(ins);
        } catch (IOException e) {
            logger.warn(String.format("Couldn't open %s ", configPath), e);
        }
        return this;
    }

    /**
     * Read the in-core properties to derive a path
     *
     * @param configKey      property key
     * @param pathRootEnvVar environment variable giving parent of configKey value
     * @return resulting Path if it exists, null otherwise
     */
    private Path PathObjectFromProperty(String configKey, String pathRootEnvVar)
    {
        String configPathValue = _Properties.getProperty(configKey);
        return GetPathWithEnv(configPathValue, pathRootEnvVar);
    }

    public static Path GetPathWithEnv(final String configPathValue, String ... pathRootEnvVar) {
        String resolvedEnv =
                pathRootEnvVar == null || pathRootEnvVar.length == 0 ||  StringUtils.isEmpty(pathRootEnvVar[0]) ?
                UserHomeEnv :
                pathRootEnvVar[0];

        if (StringUtils.isBlank(configPathValue)) return null;
        Path configPath = toAbsolutePath(resolvedEnv, configPathValue);

        if (!Files.exists(configPath))
            return null;
        return configPath;
    }

    public static Path toAbsolutePath(final String pathRootEnvVar, final String configPathValue) {
        Path configPath = Paths.get(configPathValue);
        if (!configPath.isAbsolute()) {
            String pathHome = Paths.get(System.getProperty(pathRootEnvVar)).toAbsolutePath().toString();
            configPath = Paths.get(pathHome, configPathValue);
        }
        return configPath;
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
            DumpProperties(String.format("----- External %d  Properties Merge", externalProperties.size() ));
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
    public PropertyManager PutProperty(final String key, final String value) {
        _Properties.setProperty(key, value);
        return _instance;
    }

    /**
     * Merge File resource
     *
     * @param resourcePath Path on file stream
     * @return modified instance
     */
    public PropertyManager MergeResourceFile(String resourcePath) throws IOException {
        try (InputStream inputStream = InputFileResource(resourcePath)) {
            LoadProperties(inputStream);
        }
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

    private void DumpProperties(String headerFooter) {

        String header = StringUtils.isEmpty(headerFooter) ?  "--- BEGIN " : String.format("--- BEGIN %s",
                headerFooter);
        String footer = StringUtils.isEmpty(headerFooter) ?  "--- BEGIN " : String.format("--- END %s",
                headerFooter);
        logger.debug(header);
        logger.debug("Existing properties with length {}", _Properties.size());
        _Properties.forEach((k, v) -> logger.debug("key :{}: value :{}: ",k ,v ));
        logger.debug(footer);

    }

    /**
     * Create hard wired properties
     *
     * @return new default properties
     */
    private Properties BuildDefaultProperties() {
        return new Properties() {
            {
                // jimk asset-manager-158 - looking at resources, they use the simple, not the fully qualified path name
                put("FileSequence.SequenceLength", "4");
                put(UserConfigPathKey, UserConfigPathValue);
            }
        };
    }

    /**
     * No public access. Use accessors by type (getPropertyInt, getPropertyString)
     */
    private final Properties _Properties;

    /**
     * Read integer resource from in core dictionary
     *
     * @param key unique name
     * @return value of key from in-memory properties
     */
    public int getPropertyInt(String key)
    {
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

        String rc = _Properties.getProperty(key);
        if (StringUtils.isEmpty(rc)) {
            rc = "";
        }
        return rc;
    }

    /**
     * get a property's value and map it to a real file path
     *
     * @param key property identifier
     * @return absolute path, relative to "user.dir", of the key's value
     */
    public Path getPropertyPath(String key) {
        String val = getPropertyString(key);
        return toAbsolutePath("user.home", val);
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
        _instance = new PropertyManager();
        return _instance;
    }

    /**
     * Use the existing property manager instance or die
     *
     * @return existing properties manager
     * @throws IllegalStateException if invoked before constructed
     */
    public static PropertyManager getInstance() throws IllegalStateException {
        if (_instance == null) {
            throw new IllegalStateException("Property Manager invoked before construction. Contact BDRC support");
        }
        return _instance;
    }

    // endregion
}
