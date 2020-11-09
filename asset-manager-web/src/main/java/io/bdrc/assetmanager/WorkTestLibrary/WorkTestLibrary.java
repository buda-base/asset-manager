package io.bdrc.assetmanager.WorkTestLibrary;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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


    @OneToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Config config;

    public WorkTestLibrary(String path){
        this._path = path ;
    }

    @OneToMany(mappedBy = "workTestLibrary", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private Set<WorkTest> _workTests = new HashSet<>();

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
        // _workTests.forEach(wt -> wt.setWorkTestLibrary(this));
    }


    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
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
