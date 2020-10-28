package io.bdrc.assetmanager.WorkTestLibrary;


import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.config.Config;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class WorkTestLibrary {

    private @Id @GeneratedValue Long id;

    private String _path ;
   // private List<WorkTestParameter> workTests ;

    protected WorkTestLibrary() {}


    @OneToOne(targetEntity = Config.class, cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
    private Config _config;

    @OneToMany(mappedBy = "_workTestLibrary", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<WorkTest> _workTests = new HashSet<>();

    public WorkTestLibrary(String path){
        this._path = path ;
    }

    // region property accessors
    public Long getId() { return id ;}

    public String getPath() { return _path ; }

    public void setPath(String newValue) {
        _path = newValue;
    }

    public Set<WorkTest> getWorkTests() {
        return _workTests;
    }
    public void setWorkTests(final Set<WorkTest> workTests) {
        _workTests = workTests;
    }


    public Config getConfig() {
        return _config;
    }

    public void setConfig(final Config config) {
        _config = config;
    }

    // endregion

    // region overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTestLibrary tl  = (WorkTestLibrary) o;
        return Objects.equals(id, tl.id) &&
                Objects.equals(_path, tl.getPath());
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, _path);
    }
    // endregion
}
