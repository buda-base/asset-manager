package io.bdrc.assetmanager.WorkTest;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
public class WorkTest {

    // region fields
    private @Id
    @GeneratedValue
    Long id;

    private String testName ;

    // Persist auto calls the repository to save
    @OneToMany ( mappedBy = "workTest",cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
    private Set<WorkTestParameter> workTestParameters ;
    //endregion

    //region constructors

    protected WorkTest() {}

    public WorkTest(String testName) {
        this.workTestParameters = new HashSet<>();
        setTestName(testName);
    }

    // endregion

    // region methods
    public void addWorkTestParameter( WorkTestParameter workTestParameter)
    {
        workTestParameters.add(workTestParameter);
    }
    //endregion

    // region field accessors
    public Set<WorkTestParameter> getWorkTestParameters() {
        return workTestParameters;
    }

    public void setWorkTestParameters(final Set<WorkTestParameter> workTestParameters) {
        this.workTestParameters = workTestParameters;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(final String testName) {
        this.testName = testName;
    }

    // endregion

    // region overrides
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTest workTest = (WorkTest) o;
        return Objects.equals(id, workTest.id) &&
                Objects.equals(testName, workTest.testName) &&
                Objects.equals(workTestParameters, workTest.workTestParameters) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workTestParameters);
    }
    // endregion
}
