package io.bdrc.audit.shell.diagnostics;

import org.apache.commons.lang3.StringUtils;

import java.time.temporal.ValueRange;
import java.util.*;


/**
 * Quasi-entity to store values of diagnostic controls
 */
public class DiagnosticEntity extends Hashtable<String, List<String>> {

    /**
     * Call with a reduced set of system properties
     *
     * @param properties The properties related to diagnostics
     */
    public DiagnosticEntity(Properties properties, String charSep) {
        properties
                .entrySet()
                .stream()
                .filter(x -> x.getValue() != null)
                .forEach(p -> {
                    List<String> tmpVal = Arrays.asList(p.getValue().toString().split(charSep));
                    this.put(p.getKey().toString(), tmpVal);
                });
    }

    /**
     * Helper function which tests if there's anything "really" in the list
     * @param value list of strings to test
     * @return true if there are multiple entries in the list, or if there is exactly one list entry, which is non
     * empty.
     */
    public static boolean hasValue(List<String> value) {
        return (value != null
                && (
                // if value.size == 0, this OR test will also fail
                (value.size() == 1 && value.get(0) != null && !StringUtils.isEmpty(value.get(0)))
                        || value.size() > 1
        ));
    }
}
