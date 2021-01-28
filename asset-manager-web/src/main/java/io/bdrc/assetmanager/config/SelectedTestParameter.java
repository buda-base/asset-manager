package io.bdrc.assetmanager.config;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Objects;

/**
 * Selected test parameter
 */
@Entity
public class SelectedTestParameter {

    String name;
    String value;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(targetEntity = SelectedTest.class, fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonBackReference
    private SelectedTest selectedTest;

    public SelectedTestParameter(final String name, final String value, final SelectedTest selectedTest) {
        this.setName(name);
        this.setValue(value);
        this.selectedTest = selectedTest;
    }

    protected SelectedTestParameter() {}

    //region Accessors
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    //endregion

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    // endregion
    // region Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectedTestParameter oTestParameter = (SelectedTestParameter) o;
        SelectedTest oTest = oTestParameter.selectedTest;

        // either both null or both not null
        boolean wtHasValue = (!Objects.equals(this.selectedTest, null) && !Objects.equals(oTest, null));

        return
                Objects.equals(this.getName(), oTestParameter.getName())
                        && Objects.equals(this.getValue(), oTestParameter.getValue())
                        && wtHasValue
                ;
    }
}
