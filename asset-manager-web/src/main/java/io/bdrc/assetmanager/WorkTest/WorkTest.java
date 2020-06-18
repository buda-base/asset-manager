package io.bdrc.assetmanager.WorkTest;


import io.bdrc.assetmanager.InvalidObjectData;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Set;


@Entity
public class WorkTest {

    // region fields
    private @Id
    @GeneratedValue
    Long id;

    // TODO:  Need to map this to a jar + test name combination
    @NaturalId
    private String testName;

    // Persist auto calls the repository to save
    @OneToMany(mappedBy = "workTest", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<WorkTestParameter> workTestParameters;

    @Transient
    Hashtable<String, String> argKV = new Hashtable<>();
    //endregion

    //region constructors

    protected WorkTest() {
    }

    public WorkTest(String testName) {
        this.workTestParameters = new HashSet<>();
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
    public void addWorkTestParameter(WorkTestParameter workTestParameter) throws InvalidObjectData
    {
        enforceUniqueConstraint(workTestParameter);
        workTestParameters.add(workTestParameter);
        argKV.put(workTestParameter.getName(), workTestParameter.getValue());
    }
    //endregion

    // region field accessors
    public Set<WorkTestParameter> getWorkTestParameters() {
        return workTestParameters;
    }

    public void setWorkTestParameters(final Set<WorkTestParameter> workTestParameters) throws InvalidObjectData {
        enforceUniqueConstraint(workTestParameters);
        this.workTestParameters = workTestParameters;

        // Add back pointer
        // you can't put a routine which throws into a .forEach statement
        for (WorkTestParameter wtp : this.workTestParameters) {
            wtp.setWorkTest(this);
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
     * must have unique names. Uses Hashtable property that keys must be unique.
     * This overload tests the internal set for consistency. The
     * overload (final WorkTestParameter workTestParamater) tests an
     * individual WorkTestParameter against the existing set5
     *
     * @param workTestParameters complete set of work test parameters
     * @throws InvalidObjectData when any workTestParameters have a duplicate name
     */
    private void enforceUniqueConstraint(final Set<WorkTestParameter> workTestParameters) throws InvalidObjectData
    {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        Hashtable<String, String> setNames = new Hashtable<>();
        workTestParameters.forEach(x ->
                setNames.put(x.getName(), ""));

        // did hashtable replace a value?
        if (setNames.keySet().size() != workTestParameters.size()) {
            throw new InvalidObjectData("WorkTestParameter Collection has duplicate elements");
        }
    }

    /**
     * Test an individual WorkTestParameter for uniqueness in this' current set
     *
     * @param workTestParameter test object - getName() must not be found in existing
     * @throws InvalidObjectData when a duplicate name would be created
     */

    // TODO: Left off here realizing that this could be an update operation, so I don't really care.
    // Have to change 'add' to 'addOrUpdate'
    private void enforceUniqueConstraint(final WorkTestParameter workTestParameter) throws InvalidObjectData
    {
        try {
            argKV.put(workTestParameter.getName(), "");

            // We don't set this here
            argKV.remove(workTestParameter.getName());
        } catch (Exception e) {
            throw new InvalidObjectData(String.format("Cannot add Duplicate work Test Parameter Name %s to existing",
                    workTestParameter.getName()), e);
        }
    }
    // endregion
}
