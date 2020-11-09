package io.bdrc.assetmanager.config;
// https://spring.io/guides/tutorials/react-and-spring-data-rest/

import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
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

    // @JsonIgnore
    @OneToOne(cascade = CascadeType.PERSIST) // ( mappedBy = "config", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private WorkTestLibrary workTestLibrary;

    // Persist auto calls the repository to save
    // Just use test Ids, not entities, to select tests
    @Transient
    private Set<WorkTest> workTests = new HashSet<>();

    protected Config() {
    }

    public Config(WorkTestLibrary workTestLibrary, List<WorkTest> workTests) {
        this.setworkTestLibrary(workTestLibrary);
        this.setWorkTests(workTests);
    }

    // TODO: Use copy constructor pattern to copy subclasses and lists
    // https://vladmihalcea.com/clone-duplicate-entity-jpa-hibernate/
    /**
     * Copy Constructor
     * @param source copy operand
     */
    public Config(Config source) {
        this.setId(source.getId());
        this.setworkTestLibrary(source.getworkTestLibrary());
        this.setWorkTests(source.getWorkTests());
    }

    public Config(WorkTestLibrary workTestLibrary, final Set<WorkTest> workTests) {
        // bug: have to set workTests config here
        setWorkTests(workTests);
       setworkTestLibrary(workTestLibrary);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id ;}

    public WorkTestLibrary getworkTestLibrary() { return workTestLibrary;}
    public void setworkTestLibrary(WorkTestLibrary newValue) { workTestLibrary = newValue ; }

    public Set<WorkTest> getWorkTests()
    {
        return this.workTests;
    }

    public void setWorkTests(Set<WorkTest> workTests) {
        this.workTests = workTests;
    }

    public void setWorkTests(List<WorkTest> workTests) {
        this.setWorkTests(new HashSet<>(workTests));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(id, config.id) &&
                Objects.equals(workTestLibrary, config.workTestLibrary) &&
                Objects.equals(workTests, config.workTests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workTestLibrary);
    }


}
