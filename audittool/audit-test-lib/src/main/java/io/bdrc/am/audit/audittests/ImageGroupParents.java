package io.bdrc.am.audit.audittests;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import static io.bdrc.am.audit.audittests.TestArgNames.ARC_GROUP_PARENT;
import static io.bdrc.am.audit.audittests.TestArgNames.DERIVED_GROUP_PARENT;


/**
 * ImageGroupParents is a specific path base test where the names of special "imageGroup"
 * folders are passed in
 */
abstract public class ImageGroupParents extends PathTestBase {

    ImageGroupParents(String testName) {
        super(testName);
    }


    // Special case folders, define parents of image groups. Only image group folders have to
    // match some tests
    protected ArrayList<String> _imageGroupParents = new ArrayList<>();

    private ImageGroupParentsVisited _visitedImageGroupParents ;

    // Extract only the values for these properties. For example, see audit-test-shell.scripts/shell.properties
    private final ArrayList<String> _propertyKeys = new ArrayList<String>() {{
        add(ARC_GROUP_PARENT);
        add(DERIVED_GROUP_PARENT);
    }};

    // region overrides

    /**
     * FileSequence parameters:
     * 1: path: String
     * 2: HashTable<String,String> Keyword args
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

        super.setParams(params);
        _imageGroupParents = (ArrayList<String>) filterProperties(keywordArgParams, _propertyKeys);
        _visitedImageGroupParents = new ImageGroupParentsVisited(_imageGroupParents);

    }

    /**
     * Load into a private field only the kwargs we need
     *
     * @param argDict  input properties
     * @param seekList properties to load into this class
     * @return the values of the properties in argDict which were filtered
     */
    @SuppressWarnings("unchecked")
    private Collection<String> filterProperties(Object argDict, Collection<String> seekList) {

        Hashtable<String, String> parentProperties = (Hashtable<String, String>) (argDict);

        ArrayList<String> foundValues = new ArrayList<>(seekList.size());
        parentProperties.forEach((k, v) -> {
            if (seekList.contains(k)) foundValues.add(v);
        });
        return foundValues;
    }

    /**
     * Mark an image group parent as visited
     * @param anImageGroupParent name to tag as visited
     */
    protected void  MarkVisited(String anImageGroupParent){
        _visitedImageGroupParents.MarkVisited(anImageGroupParent);

    }

    public void ReportUnvisited(Logger logger, boolean isError) {

        // Were some image group parents skipped?
        List<String> missingParents = _visitedImageGroupParents.getByVisitState(false);
        if (missingParents.size() > 0) {
            StringBuilder sb = new StringBuilder();
            missingParents.forEach(x -> sb.append(String.format("%s ", x)));
            String badParents = String.format("Image group parent(s) '%s' not found.", sb.toString());
            if (isError) {
                logger.error(badParents);
                FailTest(LibOutcome.ROOT_NOT_FOUND, badParents);
            } else {
                logger.warn(badParents);
                MarkTestNotRun(LibOutcome.ROOT_NOT_FOUND, badParents);
            }
        }
    }

}
