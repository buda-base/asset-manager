package io.bdrc.assetmanager.WorkTest;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class RunnableTestParameter {

    private @Id
    @GeneratedValue
    Long id;
    private String paramName;
    private String paramValue;

    @ManyToOne (targetEntity = RunnableTest.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private RunnableTest runnableTest;

    protected RunnableTestParameter() {
    }

    public RunnableTestParameter(String name, String value) {
        this.paramName = name;
        this.paramValue = value;

    }

    public RunnableTestParameter(String name, String value, RunnableTest runnableTest) {
        this.paramName = name;
        this.paramValue = value;
        this.setRunnableTest(runnableTest);
    }

    /**
     * Copy constructor
     *
     * @param source RunnableTestParameter to copy
     *               Does not copy work test, as that would violate the testName unique constraint
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public RunnableTestParameter(RunnableTestParameter source) {
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

    public void setName(String newValue) {
        paramName = newValue;
    }

    public void setValue(String newValue) {
        paramValue = newValue;
    }


    // Done: TO DO : I left off here thinking about cascading throws declarations
    /**
     * Moves this testParameter from its current test to a new test
     *
     * @param newValue new containing test
     *                 removes test from current parent
     */
    public void setRunnableTest(RunnableTest newValue) {
        if (newValue == null) {
            this.deleteWorkTest();
        } else {
            runnableTest = newValue;
            newValue.replaceWorkTestParameter(this);
        }
    }

    public RunnableTest getRunnableTest() {
        return runnableTest;
    }

    public void deleteWorkTest()
    {
        if (this.runnableTest != null) {
            this.runnableTest.removeWorkTestParameter(this);
        }
        this.runnableTest = null;
    }

    // endregion
    // region Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunnableTestParameter workTestParameter = (RunnableTestParameter) o;
        RunnableTest wt = this.runnableTest;
        RunnableTest wto = workTestParameter.runnableTest;

        // either both null or both not null
        boolean wtHasValue = (!Objects.equals(wt, null) && !Objects.equals(wto, null));

        return
                Objects.equals(paramName, workTestParameter.paramName)
                        && Objects.equals(paramValue, workTestParameter.paramValue)
                        && wtHasValue
                        && Objects.equals(runnableTest.getTestName(),
                        workTestParameter.runnableTest.getTestName())
                ;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, paramName, paramValue);
    }
    // endregion
}
