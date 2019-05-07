package io.bdrc.am.audit.audittests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

/**
 * ImageGroupParents is a specific path base test where the names of special "imageGroup"
 * folders are passed in
 */
abstract public class ImageGroupParents extends PathTestBase {

    public ImageGroupParents(String testName) {
        super(testName);
    }


    // Special case folders, define parents of image groups. Only image group folders have to
    // match sequence tests
    protected ArrayList<String> _imageGroupParents = new ArrayList<>();

    // Extract only the values for these properties. For example, see audit-test-shell.scripts/shell.properties
    protected final ArrayList<String> _propertyKeys = new ArrayList<String>() {{
        add("ArchiveImageGroupParent");
        add("DerivedImageGroupParent");
    }};

    // region overrides

    /**
     * FileSequence parameters:
     * 1: path: String
     * 2: HashTable<String,String> Properties
     *
     * @param params array of parameters, implementation dependent
     * @throws IllegalArgumentException when arguments dont contain a hashset of values
     */
    public void setParams(Object... params) throws IllegalArgumentException {
        if ((params == null) || (params.length < 2)) {
            throw new IllegalArgumentException(String.format("Audit test :%s: Required Arguments path, and " +
                            "propertyDictionary not given.",
                    getTestName()));
        }
        super.setParams(params[0]);

        _imageGroupParents = (ArrayList<String>) filterProperties(params[1], _propertyKeys);

    }

    @SuppressWarnings("unchecked")
    private Collection<String> filterProperties(Object argDict, Collection<String> seekList) {

        Hashtable<String, String> parentProperties = (Hashtable<String, String>) (argDict);

        ArrayList<String> foundValues = new ArrayList<>(seekList.size());
        parentProperties.forEach((k, v) -> {
            if (seekList.contains(k)) foundValues.add(v);
        });
        return foundValues;
    }
}
