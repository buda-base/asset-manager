package io.bdrc.assetmanager.WorkTest;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class RunnableTestParameter {

    private @Id
    @GeneratedValue
    Long id;
    private String paramName;
    private String paramValue;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    RunnableTest _runnableTest;

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

    public RunnableTest getworkTest() {
        return _runnableTest;
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
            _runnableTest = newValue;
            newValue.replaceWorkTestParameter(this);
        }
    }

    public void deleteWorkTest()
    {
        if (this._runnableTest != null) {
            this._runnableTest.removeWorkTestParameter(this);
        }
        this._runnableTest = null;
    }

    // endregion
    // region Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunnableTestParameter workTestParameter = (RunnableTestParameter) o;
        RunnableTest wt = this._runnableTest;
        RunnableTest wto = workTestParameter._runnableTest;

        // either both null or both not null
        boolean wtHasValue = (!Objects.equals(wt, null) && !Objects.equals(wto, null));

        return
                Objects.equals(paramName, workTestParameter.paramName)
                        && Objects.equals(paramValue, workTestParameter.paramValue)
                        && wtHasValue
                        && Objects.equals(_runnableTest.getTestName(),
                        workTestParameter._runnableTest.getTestName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, paramName, paramValue);
    }
    // endregion
}
