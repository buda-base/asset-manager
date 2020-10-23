package io.bdrc.assetmanager.config;
// https://spring.io/guides/tutorials/react-and-spring-data-rest/

import io.bdrc.assetmanager.InvalidObjectData;
import io.bdrc.assetmanager.WorkTest.WorkTest;

import javax.persistence.*;
import java.util.*;

/**
 * Config entity
 */

@Entity
public class Config {

    private @Id
    @GeneratedValue
    Long id;

    private String _workTestLibrary;

    // Persist auto calls the repository to save
    @OneToMany(mappedBy = "config", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<WorkTest> _workTests = new HashSet<>();

    protected Config() {
    }

    /**
     * Copy Constructor
     * @param source
     * @return copy
     */
    public Config(Config source) throws InvalidObjectData {
        this.set_workTestLibrary(source.get_workTestLibrary());
        this.setWorkTests(source.getWorkTests());
    }

    public Config(String workTestLibrary, final Set<WorkTest> workTests) {
       this.setWorkTests(workTests);
       set_workTestLibrary(workTestLibrary);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id ;}

    public String get_workTestLibrary() { return _workTestLibrary;}
    public void set_workTestLibrary(String newValue) { _workTestLibrary = newValue ; }

    public Set<WorkTest> getWorkTests()
    {
        return this._workTests;
    }

    public void setWorkTests(Set<WorkTest> workTests) {
        this._workTests = workTests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(id, config.id) &&
                Objects.equals(_workTestLibrary, config._workTestLibrary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, _workTestLibrary);
    }


}
