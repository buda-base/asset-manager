package io.bdrc.assetmanager.WorkTest;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
//    // @OneToMany(mappedBy = "workTest", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @OneToMany(targetEntity = RunnableTestParameter.class, mappedBy = "runnableTest", cascade = CascadeType.PERSIST,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    private  Set<RunnableTestParameter> runnableTestParameters = new HashSet<>();

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
     * @param source source runnableTest
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public RunnableTest(RunnableTest source) throws InvalidObjectData {
        this.setTestName(source.getTestName());
         this.setRunnableTestParameters(source.getRunnableTestParameters());
    }

    // endregion

    // region methods

    /**
     * Adds or replaces a workTestParameter
     *
     * @param runnableTestParameter new or updated RunnableTestParameter
     */
    public void replaceWorkTestParameter(RunnableTestParameter runnableTestParameter)
    {
        List<RunnableTestParameter> wtpf =
        runnableTestParameters.stream()
                .filter(x -> x.getName().equals(runnableTestParameter.getName()))
                .collect(Collectors.toList());
        wtpf.forEach(this::removeWorkTestParameter);

        // runnableTestParameter.setRunnableTest(this);
        // Dont add directly.
        // let the child find its parent
        this.runnableTestParameters.add(runnableTestParameter);
    }

    /**
     * remove a RunnableTestParameter, don't worry if it's not there
     *
     * @param testParameter to be removed
     */
    public void removeWorkTestParameter(RunnableTestParameter testParameter)
    {
        runnableTestParameters.remove(testParameter);
    }
    //endregion

    // region field accessors
    public Set<RunnableTestParameter> getRunnableTestParameters() {
        return runnableTestParameters;
    }

    /**
     * Replace existing test parameters with new set
     *
     * @param runnableTestParameters new set of runnableTestParameters
     * @throws InvalidObjectData when the input set has duplicate test names
     */
    public void setRunnableTestParameters(final Set<RunnableTestParameter> runnableTestParameters) throws InvalidObjectData {
        enforceUniqueConstraint(runnableTestParameters);

        for (RunnableTestParameter wtp : runnableTestParameters)
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
                Objects.equals(testName, runnableTest.testName)
                &&           Objects.equals(runnableTestParameters, runnableTest.runnableTestParameters)
                ;
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
