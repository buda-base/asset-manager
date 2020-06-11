package io.bdrc.assetmanager.worktestparameter;


import io.bdrc.assetmanager.WorkTest.WorkTest;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
public class WorkTestParameter {

    private @Id @GeneratedValue Long id;
    private String paramName ;
    private String paramValue;

    @ManyToOne
    WorkTest workTest ;  //bidirectional

    protected WorkTestParameter(){}

    public WorkTestParameter(String name, String value, WorkTest workTest) {
        this.paramName = name;
        this.paramValue = value;
        this.workTest = workTest;
    }
        // region accessors
        public String getName() { return paramName ; }
        public String getValue() { return paramValue ;}
        public Long getId() { return id ;}
        public WorkTest getWorkTest()  { return workTest ; }

        public void setName(String newValue) { paramName = newValue ; }
        public void setValue(String newValue) { paramValue = newValue ; }

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
