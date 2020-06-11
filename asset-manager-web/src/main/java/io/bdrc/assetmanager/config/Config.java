package io.bdrc.assetmanager.config;
// https://spring.io/guides/tutorials/react-and-spring-data-rest/

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

/**
 * Config entity
 */

@Entity
public class Config {

    private @Id
    @GeneratedValue
    Long id;

    private String workTestLibrary;

    protected Config() {

    }

    public Config(String workTestLibrary) {
        this.workTestLibrary = workTestLibrary;
    }
˚
    public Long getId() {˚
        return id;
    }
    public void setId(Long id) { this.id = id ;}

    public String getWorkTestLibrary() { return workTestLibrary ;}
    public void setWorkTestLibrary(String newValue) { workTestLibrary = newValue ; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(id, config.id) &&
                Objects.equals(workTestLibrary, config.workTestLibrary);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, workTestLibrary);
    }


}
