package io.bdrc.assetmanager.config;
// https://spring.io/guides/tutorials/react-and-spring-data-rest/

import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Config entity
 */

@Entity
public class Config {

    private @Id
    @GeneratedValue
    Long id;

    @OneToOne(targetEntity = WorkTestLibrary.class, mappedBy = "_config", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private WorkTestLibrary _workTestLibrary;

    // Persist auto calls the repository to save
    @OneToMany(mappedBy = "config", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<WorkTest> _workTests = new HashSet<>();

    protected Config() {
    }

    // TODO: Use copy constructor pattern to copy subclasses and lists
    // https://vladmihalcea.com/clone-duplicate-entity-jpa-hibernate/
    /**
     * Copy Constructor
     * @param source copy operand
     */
    public Config(Config source) {
        this.set_workTestLibrary(source.get_workTestLibrary());
        this.setWorkTests(source.getWorkTests());
    }

    public Config(WorkTestLibrary workTestLibrary, final Set<WorkTest> workTests) {
        // bug: have to set workTests config here
        if (null != workTests) {
            workTests.forEach(w -> w.setConfig(this));
            this.setWorkTests(workTests);
        }
       set_workTestLibrary(workTestLibrary);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id ;}

    public WorkTestLibrary get_workTestLibrary() { return _workTestLibrary;}
    public void set_workTestLibrary(WorkTestLibrary newValue) { _workTestLibrary = newValue ; }

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
                Objects.equals(_workTestLibrary, config._workTestLibrary) &&
                Objects.equals(_workTests, config._workTests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, _workTestLibrary);
    }


}
