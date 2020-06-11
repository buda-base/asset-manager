package io.bdrc.assetmanager.WorkTestLibrary;


import javax.persistence.*;
import java.util.Objects;

@Entity
public class WorkTestLibrary {

    private @Id @GeneratedValue Long id;

    private String _path ;
   // private List<WorkTestParameter> workTests ;

    protected WorkTestLibrary() {}

    // TODO: Figure out how to do container members
//    public WorkTestLibrary(String path, final List<WorkTestParameter> workTests){
//        this._path = path ;
//        this.workTests = workTests;
//    }


    public WorkTestLibrary(String path){
        this._path = path ;
    }

    // region property accessors
    public Long getId() { return id ;}

    public String getPath() { return _path ; }

    public void setPath(String newValue) {
        _path = newValue;
    }

    // endregion

    // region overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTestLibrary tl  = (WorkTestLibrary) o;
        return Objects.equals(id, tl.id) &&
                Objects.equals(_path, tl.getPath());
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, _path);
    }
    // endregion
}
