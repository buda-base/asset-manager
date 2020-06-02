package io.bdrc.assetmanager.repositories;

import io.bdrc.assetmanager.entities.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {

    List<Employee> FindByFirstName();
    }
