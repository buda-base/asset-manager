package io.bdrc.assetmanager.config;
// https://spring.io/guides/tutorials/react-and-spring-data-rest/

import com.fasterxml.jackson.annotation.JsonManagedReference;
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


    @OneToMany(mappedBy = "config", targetEntity = SelectedTest.class, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<SelectedTest> selectedTests = new HashSet<>();

    protected Config() {
    }

    public Config(WorkTestLibrary workTestLibrary, List<RunnableTest> selectedTests) {
        this.setWorkTestLibrary(workTestLibrary);
        this.setTests(selectedTests);
    }
    /**
     * Ctor. create a config from a library and a selection of its tests
     * @param workTestLibrary Jar file containing the tests
     * @param selectedTests subset of RunnableTest objects the library supports
     */
    public Config(WorkTestLibrary workTestLibrary, final Set<SelectedTest> selectedTests) {
        // bug: have to set workTests config here
        setSelectedTests(selectedTests);
        setWorkTestLibrary(workTestLibrary);
    }

    // TODO: Use copy constructor pattern to copy subclasses and lists
    // https://vladmihalcea.com/clone-duplicate-entity-jpa-hibernate/
    /**
     * Copy Constructor
     * @param source copy operand
     */
    public Config(Config source) {
        this.setId(source.getId());
        this.setWorkTestLibrary(source.getWorkTestLibrary());
        this.setSelectedTests(source.getSelectedTests());
    }

    //region Accessors

    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id ;}

    public WorkTestLibrary getWorkTestLibrary() { return workTestLibrary;}
    public void setWorkTestLibrary(WorkTestLibrary newValue) { workTestLibrary = newValue ; }

    public Set<SelectedTest> getSelectedTests()
    {
        return this.selectedTests;
    }

    public void setSelectedTests(Set<SelectedTest> selectedTests) {
        this.selectedTests = selectedTests;
    }

    public void setTests(Set<RunnableTest> runnableTests) {
        Set<SelectedTest> inTests = new HashSet<>();
        runnableTests.forEach(x -> {
            SelectedTest st = SelectedTest.fromRunnable(x);
            st.setConfig(this);
            inTests.add(st);
        });
        this.setSelectedTests(inTests);
    }

    public void setTests(List<RunnableTest> runnableTestList) {
        this.setTests(new HashSet<>(runnableTestList));
    }
// endregion
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(id, config.id) &&
                Objects.equals(workTestLibrary, config.workTestLibrary) &&
                Objects.equals(selectedTests, config.selectedTests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workTestLibrary);
    }


}
