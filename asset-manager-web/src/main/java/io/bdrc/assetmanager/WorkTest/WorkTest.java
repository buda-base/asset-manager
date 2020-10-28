package io.bdrc.assetmanager.WorkTest;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.bdrc.assetmanager.InvalidObjectData;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;
import io.bdrc.assetmanager.config.Config;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
public class WorkTest {

    // region fields
    private @Id
    @GeneratedValue
    Long id;

    private String testName;

    // Thanks to SO for de-recursing
    //https://stackoverflow.com/questions/13785530/serialize-listobject-with-manytoone-onetomany-relational-to-json
    // Persist auto calls the repository to save
    @OneToMany(mappedBy = "workTest", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonManagedReference
    private final Set<WorkTestParameter> workTestParameters = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JsonBackReference
    Config config;  //bidirectional
    //endregion

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonBackReference
    WorkTestLibrary _workTestLibrary;

    //region constructors

    protected WorkTest() {
    }

    public WorkTest(String testName) {
        setTestName(testName);
    }

    /**
     * Copy constructor
     *
     * @param source source workTest
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public WorkTest(WorkTest source) throws InvalidObjectData {
        this.setTestName(source.getTestName());
         this.setWorkTestParameters(source.getWorkTestParameters());
    }

    // endregion

    // region methods

    /**
     * Adds or replaces a workTestParameter
     *
     * @param workTestParameter new or updated WorkTestParameter
     */
    public void replaceWorkTestParameter(WorkTestParameter workTestParameter)
    {
        List<WorkTestParameter> wtpf =
        workTestParameters.stream()
                .filter(x -> x.getName().equals(workTestParameter.getName()))
                .collect(Collectors.toList());
        wtpf.forEach(this::removeWorkTestParameter);

        // workTestParameter.setWorkTest(this);
        // Dont add directly.
        // let the child find its parent
        this.workTestParameters.add(workTestParameter);
    }

    /**
     * remove a WorkTestParameter, don't worry if it's not there
     *
     * @param workTestParameter to be removed
     */
    public void removeWorkTestParameter(WorkTestParameter workTestParameter)
    {
        workTestParameters.remove(workTestParameter);
    }
    //endregion

    // region field accessors
    public Set<WorkTestParameter> getWorkTestParameters() {
        return workTestParameters;
    }

    /**
     * Replace existing test parameters with new set
     *
     * @param workTestParameters new set of workTestParameters
     * @throws InvalidObjectData when the input set has duplicate test names
     */
    public void setWorkTestParameters(final Set<WorkTestParameter> workTestParameters) throws InvalidObjectData {
        enforceUniqueConstraint(workTestParameters);

        for (WorkTestParameter wtp : workTestParameters)
        {
            WorkTestParameter newWtp = new WorkTestParameter(wtp);
            newWtp.setWorkTest(this);

        }
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(final String testName) {
        this.testName = testName;
    }


    public WorkTestLibrary getWorkTestLibrary() {
        return _workTestLibrary;
    }

    public void setWorkTestLibrary(final WorkTestLibrary workTestLibrary) {
        _workTestLibrary = workTestLibrary;
    }
    // endregion

    // region overrides
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTest workTest = (WorkTest) o;

        return
                Objects.equals(testName, workTest.testName) &&
                        Objects.equals(workTestParameters, workTest.workTestParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testName);
    }
    // endregion

    // region private members

    /**
     * enforces that all the inputs in a candidate input set
     * must have unique names.
     * This overload tests the internal set for consistency. The
     * overload (final WorkTestParameter workTestParameter) tests an
     * individual WorkTestParameter against the existing set
     *
     * @param workTestParameters complete set of work test parameters
     * @throws InvalidObjectData when any workTestParameters have a duplicate name
     */
    private void enforceUniqueConstraint(final Set<WorkTestParameter> workTestParameters) throws InvalidObjectData
    {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        HashSet<String> setNames = new HashSet<>();
        workTestParameters.forEach(x ->
                setNames.add(x.getName()));

        // did hashtable replace a value?
        if (setNames.size() != workTestParameters.size()) {
            throw new InvalidObjectData("WorkTestParameter Collection has duplicate elements");
        }
    }
    // endregion
}
