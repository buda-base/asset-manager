package io.bdrc.assetmanager.WorkTestLibrary;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.bdrc.assetmanager.WorkTest.RunnableTest;
import io.bdrc.assetmanager.config.Config;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class WorkTestLibrary {

    private @Id @GeneratedValue Long id;

    private String _path ;

    protected WorkTestLibrary() {}


    @OneToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Config config;


    public WorkTestLibrary(String path){
        this._path = path ;
    }

    // the @JsonIgnore means that the json mapper only maps the @JSonManagedReference entities (
    // it magically deduces the name), otherwise you see collections under both
    // "runnableTests" : [...] and "_runnableTests : [ .... ]
    @OneToMany(mappedBy = "workTestLibrary", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private Set<RunnableTest> _runnableTests = new HashSet<>();

    // region property accessors
    public Long getId() { return id ;}

    public String getPath() { return _path ; }

    public void setPath(String newValue) {
        _path = newValue;
    }

    public Set<RunnableTest> getRunnableTests() {
        return _runnableTests;
    }
    public void setRunnableTests(Set<RunnableTest> runnableTestsValue) {
        _runnableTests = runnableTestsValue;
        _runnableTests.forEach(wt -> wt.setworkTestLibrary(this));
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
