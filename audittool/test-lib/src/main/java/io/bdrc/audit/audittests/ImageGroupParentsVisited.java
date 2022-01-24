package io.bdrc.audit.audittests;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * For a test, tracks which image group parents have been visited
 */
public class ImageGroupParentsVisited {

    private final Hashtable<String,Boolean> _visited = new Hashtable<>();

    public ImageGroupParentsVisited(List<String> parents) {
        parents.forEach(x-> _visited.put(x,false));
    }

    // We want this to throw if the parent isn't there.
    public void MarkVisited(String parent) {
        _visited.replace(parent, true);
    }

    public List<String> getByVisitState(Boolean visitState) {
        // Were some image group parents skipped?
        ArrayList<String> soughtMembers = new ArrayList<>(_visited.size());
        _visited.entrySet()
                .stream()
                .filter(x -> x.getValue() == visitState)
                .forEach(y -> soughtMembers.add(y.getKey()));
        return soughtMembers;
    }
}
