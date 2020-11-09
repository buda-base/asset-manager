package io.bdrc.assetmanager.config;
// https://spring.io/guides/tutorials/react-and-spring-data-rest/

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;
import org.hibernate.jdbc.Work;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Config entity
 */

@Entity
public class Config {

    private @Id
    @GeneratedValue
    Long id;

    @JsonIgnore
    @OneToOne(targetEntity = WorkTestLibrary.class, mappedBy = "config", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private WorkTestLibrary _workTestLibrary;

    // Persist auto calls the repository to save
    @JsonIgnore
    @OneToMany(mappedBy = "config", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<WorkTest> _workTests = new HashSet<>();

    protected Config() {
    }

    public Config(WorkTestLibrary workTestLibrary, List<WorkTest> workTests) {
        this.set_workTestLibrary(workTestLibrary);
        this.setWorkTests(workTests);
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
        _workTests.forEach(wt -> wt.setConfig(this));
    }

    public void setWorkTests(List<WorkTest> workTests) {
        this.setWorkTests(workTests.stream().collect(Collectors.toSet()));
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
