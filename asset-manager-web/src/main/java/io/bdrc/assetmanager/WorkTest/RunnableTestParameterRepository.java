package io.bdrc.assetmanager.WorkTest;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RunnableTestParameterRepository extends CrudRepository<RunnableTestParameter, Long> {
    // Do we get this by default?
    // List<RunnableTestParameter> findByWorkTest(WorkTest);
    // No
    // List<RunnableTestParameter> findByRunnableTest(RunnableTest runnableTest);

}
