package io.bdrc.assetmanager.config;
// https://spring.io/guides/tutorials/react-and-spring-data-rest/

import io.bdrc.assetmanager.WorkTest.RunnableTest;
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
    @OneToOne(cascade = CascadeType.ALL) // ( mappedBy = "config", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //
    // breaks maven testing persist @OneToOne(cascade = CascadeType.PERSIST) // ( mappedBy = "config", cascade =
    // CascadeType.ALL, fetch = FetchType.LAZY)
    private WorkTestLibrary workTestLibrary;

    // Persist auto calls the repository to save
    // Just use test Ids, not entities, to select tests
    @Transient
    private Set<RunnableTest> _selectedTests = new HashSet<>();

    protected Config() {
    }

    public Config(WorkTestLibrary workTestLibrary, List<RunnableTest> selectedTests) {
        this.setworkTestLibrary(workTestLibrary);
        this.setWorkTests(selectedTests);
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
        this.setSelectedTests(source.getSelectedTests());
    }

    public Config(WorkTestLibrary workTestLibrary, final Set<RunnableTest> selectedTests) {
        // bug: have to set workTests config here
        setSelectedTests(selectedTests);
       setworkTestLibrary(workTestLibrary);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id ;}

    public WorkTestLibrary getworkTestLibrary() { return workTestLibrary;}
    public void setworkTestLibrary(WorkTestLibrary newValue) { workTestLibrary = newValue ; }

    public Set<RunnableTest> getSelectedTests()
    {
        return this._selectedTests;
    }

    public void setSelectedTests(Set<RunnableTest> selectedTests) {
        this._selectedTests = selectedTests;
    }

    public void setWorkTests(List<RunnableTest> runnableTests) {
        this.setSelectedTests(new HashSet<>(runnableTests));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(id, config.id) &&
                Objects.equals(workTestLibrary, config.workTestLibrary) &&
                Objects.equals(_selectedTests, config._selectedTests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workTestLibrary);
    }


}
