package io.bdrc.assetmanager.repositories;

import io.bdrc.assetmanager.entities.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {


    }
