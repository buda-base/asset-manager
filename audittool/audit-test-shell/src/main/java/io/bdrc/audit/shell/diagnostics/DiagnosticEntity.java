package io.bdrc.audit.shell.diagnostics;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Quasi-entity to store values of diagnostic controls
 */
public class DiagnosticEntity {

    /**
     * @return internal properties
     */
    public Properties getProperties() {
        return _properties;
    }

    /**
     *
     * @param properties the diagnostic properties and values
     */
    public void setProperties(final Properties properties) {
        _properties = properties;
    }

    private Properties _properties;

    /**
     *
     * @return the char value that separates any multiply valued properties
     */
    public String getMultiValueSeparator() {
        return _multiValueSeparator;
    }

    /**
     * Set the separator of property values which have multiple values
     * @param multiValueSeparator character value separator
     */
    public void setMultiValueSeparator(final String multiValueSeparator) {
        _multiValueSeparator = multiValueSeparator;
    }

    private String _multiValueSeparator ;

    /**
     * Call with a reduced set of system properties
     * @param properties The properties related to diagnostics
     */
    public DiagnosticEntity(Properties properties, String multiValueSeparator) {
        setMultiValueSeparator(multiValueSeparator);
        setProperties(properties);
    }

    /**
     * Returns all the values of a key. If only one, is returned in the
     * @param key
     * @return list of the values in key. Caller determines length
     */
    public List<String> getValues(String key) {
        return Arrays.asList(getProperties().getProperty(key).split(getMultiValueSeparator()));
    }

}
