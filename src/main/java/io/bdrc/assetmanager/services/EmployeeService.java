package io.bdrc.assetmanager.services;

import io.bdrc.assetmanager.entities.Employee;
import io.bdrc.assetmanager.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Problematic. This is a layer over the repository which doesn't really add
 * anything. Except if we need finer grained ways of getting data by some special criteria
 */
@Service
public class EmployeeService implements IEmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService( EmployeeRepository repository) {
        this.repository = repository;
    }

    public void addEmployee(String firstName, String lastName, String position) {
        this.repository.save(new Employee(firstName, lastName, position));
    }

    public List<Employee> getbyFirstName(String firstName)
    {
        List<Employee> employees = (List <Employee>) this.repository.findAll();
        return employees.stream()
                .filter(x -> x.getFirstName().equals(firstName))
                .collect(Collectors.toList());
    }
}
