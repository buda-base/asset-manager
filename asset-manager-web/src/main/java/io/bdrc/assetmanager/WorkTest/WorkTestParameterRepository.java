package io.bdrc.assetmanager.WorkTest;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkTestParameterRepository extends CrudRepository<WorkTestParameter, Long> {
    // Do we get this by default?
    // List<WorkTestParameter> findByWorkTest(WorkTest);
    // No
    List<WorkTestParameter> findByWorkTest(WorkTest workTest);

}
