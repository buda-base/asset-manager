package io.bdrc.assetmanager.WorkTest;


import javax.persistence.*;
import java.util.Objects;

@Entity
public class WorkTestParameter {

    private @Id
    @GeneratedValue
    Long id;
    private String paramName;
    private String paramValue;

    @ManyToOne(fetch = FetchType.LAZY)
    WorkTest workTest;  //bidirectional

    protected WorkTestParameter() {
    }

    public WorkTestParameter(String name, String value) {
        this.paramName = name;
        this.paramValue = value;

    }

    public WorkTestParameter(String name, String value, WorkTest workTest)  {
        this.paramName = name;
        this.paramValue = value;
        this.setWorkTest(workTest);
    }

    /**
     * Copy constructor
     * @param source WorkTestParameter to copy
     * Does not copy work test, as that would violate the testName unique constraint
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public WorkTestParameter(WorkTestParameter source ) {
        this.paramName = source.paramName;
        this.paramValue = source.paramValue;
        // since this would violate the uniqueness constraint
    }

    // region accessors
    public String getName() {
        return paramName;
    }

    public String getValue() {
        return paramValue;
    }

    public Long getId() {
        return id;
    }

    public WorkTest getWorkTest() {
        return workTest;
    }

    public void setName(String newValue) {
        paramName = newValue;
    }

    public void setValue(String newValue) {
        paramValue = newValue;
    }

    // Done: TO DO : I left off here thinking about cascading throws declarations
    /**
     * Moves this testParameter from its current test to a new test
     * @param newValue new containing test
     * removes test from current parent
     *
     */
    public void setWorkTest(WorkTest newValue)  {
        if (newValue == null) {
            this.deleteWorkTest();
        }
        else {
            workTest = newValue;
            newValue.replaceWorkTestParameter(this);
        }
    }

    public void deleteWorkTest()
    {
        if (this.workTest != null)
        {
            this.workTest.removeWorkTestParameter(this);
        }
        this.workTest = null;
    }

    // endregion
    // region Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTestParameter workTestParameter = (WorkTestParameter) o;
        return
                Objects.equals(paramName, workTestParameter.paramName) &&
                Objects.equals(paramValue, workTestParameter.paramValue) &&

                        // dont include the complete WorkTest.equals
                Objects.equals(workTest.getTestName(), workTestParameter.workTest.getTestName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, paramName, paramValue);
    }
    // endregion
}
