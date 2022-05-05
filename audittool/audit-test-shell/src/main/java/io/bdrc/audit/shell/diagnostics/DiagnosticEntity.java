package io.bdrc.audit.shell.diagnostics;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Quasi-entity to store values of diagnostic controls
 */
public class DiagnosticEntity {

    /**
     * @return internal properties
     */
    public Map<String, String[]> getProperties() {
        return _properties;
    }

    /**
     *
     * @param properties the diagnostic properties and values
     */
    public void setProperties(final Map<String, String[]> properties) {
        _properties = properties;
    }

    private Map<String, String[]> _properties;
    /**
     * Call with a reduced set of system properties
     * @param properties The properties related to diagnostics
     */
    public DiagnosticEntity(Map<String, String[]> properties) {
        setProperties(properties);
    }

    /**
     * Returns a list of all the values of a key.
     * @param key which property to search
     * @return list of the values in key.
     */
    public List<String> getValues(String key) {
        return Arrays.asList(getProperties().get(key));
    }

}
