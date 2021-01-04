package io.bdrc.assetmanager.WorkTest;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.bdrc.assetmanager.InvalidObjectData;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
public class RunnableTest {

    // region fields
    private @Id
    @GeneratedValue
    Long id;

    private String testName;

    // Thanks to SO for de-recursing
    //https://stackoverflow.com/questions/13785530/serialize-listobject-with-manytoone-onetomany-relational-to-json
    // Persist auto calls the repository to save
    // @OneToMany(mappedBy = "workTest", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "_runnableTest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private  Set<RunnableTestParameter> _runnableTestParameters = new HashSet<>();

    //endregion

    @ManyToOne(targetEntity = WorkTestLibrary.class,  fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JsonBackReference
    WorkTestLibrary workTestLibrary;

    //region constructors

    protected RunnableTest() {
    }

    public RunnableTest(String testName) {
        setTestName(testName);
    }

    /**
     * Copy constructor
     *
     * @param source source workTest
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public RunnableTest(RunnableTest source) throws InvalidObjectData {
        this.setTestName(source.getTestName());
         this.setworkTestParameters(source.getworkTestParameters());
    }

    // endregion

    // region methods

    /**
     * Adds or replaces a workTestParameter
     *
     * @param workTestParameter new or updated RunnableTestParameter
     */
    public void replaceWorkTestParameter(RunnableTestParameter workTestParameter)
    {
        List<RunnableTestParameter> wtpf =
        _runnableTestParameters.stream()
                .filter(x -> x.getName().equals(workTestParameter.getName()))
                .collect(Collectors.toList());
        wtpf.forEach(this::removeWorkTestParameter);

        // workTestParameter.setWorkTest(this);
        // Dont add directly.
        // let the child find its parent
        this._runnableTestParameters.add(workTestParameter);
    }

    /**
     * remove a RunnableTestParameter, don't worry if it's not there
     *
     * @param workTestParameter to be removed
     */
    public void removeWorkTestParameter(RunnableTestParameter workTestParameter)
    {
        _runnableTestParameters.remove(workTestParameter);
    }
    //endregion

    // region field accessors
    public Set<RunnableTestParameter> getworkTestParameters() {
        return _runnableTestParameters;
    }

    /**
     * Replace existing test parameters with new set
     *
     * @param workTestParameters new set of workTestParameters
     * @throws InvalidObjectData when the input set has duplicate test names
     */
    public void setworkTestParameters(final Set<RunnableTestParameter> workTestParameters) throws InvalidObjectData {
        enforceUniqueConstraint(workTestParameters);

        for (RunnableTestParameter wtp : workTestParameters)
        {
            RunnableTestParameter newWtp = new RunnableTestParameter(wtp);
            newWtp.setRunnableTest(this);

        }
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


    public WorkTestLibrary getworkTestLibrary() {
        return workTestLibrary;
    }

    public void setworkTestLibrary(final WorkTestLibrary workTestLibrary) {
        this.workTestLibrary = workTestLibrary;
    }
    // endregion

    // region overrides
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunnableTest runnableTest = (RunnableTest) o;

        return
                Objects.equals(testName, runnableTest.testName) &&
                        Objects.equals(_runnableTestParameters, runnableTest._runnableTestParameters);
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
     * overload (final RunnableTestParameter workTestParameter) tests an
     * individual RunnableTestParameter against the existing set
     *
     * @param workTestParameters complete set of work test parameters
     * @throws InvalidObjectData when any workTestParameters have a duplicate name
     */
    private void enforceUniqueConstraint(final Set<RunnableTestParameter> workTestParameters) throws InvalidObjectData
    {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        HashSet<String> setNames = new HashSet<>();
        workTestParameters.forEach(x ->
                setNames.add(x.getName()));

        // did hashtable replace a value?
        if (setNames.size() != workTestParameters.size()) {
            throw new InvalidObjectData("RunnableTestParameter Collection has duplicate elements");
        }
    }
    // endregion
}
