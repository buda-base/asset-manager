package io.bdrc.assetmanager.WorkTest;


import io.bdrc.assetmanager.InvalidObjectData;

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

    public WorkTestParameter(String name, String value, WorkTest workTest) throws InvalidObjectData {
        this.paramName = name;
        this.paramValue = value;
        this.setWorkTest(workTest);
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

    // TODO : I left off here thinking about cascading throws declarations
    public void setWorkTest(WorkTest newValue) throws InvalidObjectData {
        this.workTest = newValue;

        // ACHTUNG!! Add to containers set
        newValue.addWorkTestParameter(this);

    }

    // endregion
    // region Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTestParameter workTestParameter = (WorkTestParameter) o;
        return Objects.equals(id, workTestParameter.id) &&
                Objects.equals(paramName, workTestParameter.paramName) &&
                Objects.equals(paramValue, workTestParameter.paramValue) &&
                Objects.equals(workTest, workTestParameter.workTest);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, paramName, paramValue);
    }
    // endregion
}
