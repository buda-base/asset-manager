package io.bdrc.assetmanager.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.bdrc.assetmanager.WorkTest.RunnableTest;
import io.bdrc.assetmanager.WorkTest.RunnableTestParameter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


/**
 * SelectedTest is a child of the Config. It represents an instance of a RunnableTest
 * with possibly changed parameters.
 */
@Entity
public class SelectedTest {

    @Id
    @GeneratedValue
    Long id;

    @OneToMany(targetEntity = SelectedTestParameter.class, mappedBy = "selectedTest",cascade = CascadeType.PERSIST)
    @JsonManagedReference
    @JsonIgnore
    Set<SelectedTestParameter> selectedTestParameters;

    @ManyToOne(targetEntity = Config.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonBackReference
    Config config ;


    /**
     * Builder creates a Selected Test from a runnable Test
     * @param runnableTest source of runnable test
     * @return selectedTest object
     */
    public static SelectedTest fromRunnable(final RunnableTest runnableTest) {
        SelectedTest retMe = new SelectedTest();
        Set<SelectedTestParameter> news = new HashSet<>();
        runnableTest.getRunnableTestParameters().forEach( y -> news.add(new SelectedTestParameter(y.getName(), y.getValue(),
                retMe)));
        retMe.setSelectedTestParameterSet(news);
        return retMe;
    }

    public SelectedTest() {}

    //region Accessors
    public Set<SelectedTestParameter> getSelectedTestParameterSet() {
        return selectedTestParameters;
    }

    public void setSelectedTestParameterSet(final Set<SelectedTestParameter> selectedTestParameterSet) {
        this.selectedTestParameters = selectedTestParameterSet;
    }


    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }
    //endregion

    // region Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectedTest otherSelectedTest = (SelectedTest) o;
        Config oConfig = otherSelectedTest.config;

        // either both null or both not null
        boolean hasValue = (!Objects.equals(this.config, null) && !Objects.equals(oConfig, null));

        return hasValue
                && Objects.equals(this.config,oConfig)
                && Objects.equals(this.getSelectedTestParameterSet(),
                otherSelectedTest.getSelectedTestParameterSet())
                ;
    }

    @Override
    public int hashCode() {

        Optional<Config> OC = Optional.ofNullable(config) ;
        Optional<Set<SelectedTestParameter>> SP = Optional.ofNullable(selectedTestParameters) ;
        int config_hash = OC.isPresent() ? config.hashCode() : -42;
        int sp_hash = SP.isPresent() ? selectedTestParameters.hashCode() : 42;
        return Objects.hash(id, config_hash, sp_hash);
    }
}
