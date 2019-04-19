package io.bdrc.am.audit.iaudit;

import java.util.List;

/**
 * Describe a test in detail. Used in TestDictionary
 */
public class AuditTestConfig {

    private final String _fullname;
    private final Class<?> _class;
    private final List<String> _argNames;
    private final String _key;

    /**
     * @return Test long description
     */
    public String getFullName() {
        return _fullname;
    }

    /**
     * Properties which hold arguments
     *
     * @return list of property names which hold arguments
     * <p>
     * Mainly used to specify parents of image groups.
     */
    public List<String> getArgNames() {
        return _argNames;
    }

    /**
     * Short, no space name of test.
     *
     * @return the key of the dictionary it's in.
     */
    public String getKey() {
        return _key;
    }

    /**
     * @return Actual test implementation class (not object!)
     */
    public Class<?> getTestClass() {
        return _class;
    }

    public AuditTestConfig(String fullName, List<String> argNames, String shortName, Class<?> clazz) {
        _fullname = fullName;
        _argNames = argNames;
        _key = shortName;
        _class = clazz;
    }
}
