package io.bdrc.assetmanager.repositories;

import io.bdrc.assetmanager.entities.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {


    }
