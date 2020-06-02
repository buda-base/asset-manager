package io.bdrc.assetmanager.services;

import io.bdrc.assetmanager.entities.Employee;
import io.bdrc.assetmanager.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;


    // Note were Dep Inj this instead of it being a mock bean (see tests)
    public EmployeeService(final EmployeeRepository repository) {
        this.repository = repository;
    }

    public void addEmployee(String firstName, String lastName, String position) {
        this.repository.save(new Employee(firstName, lastName, position));
    }

    public List<Employee> getbyFirstName(String firstName)
    {
        List<Employee> employees = (List <Employee>) this.repository.findAll();
        return employees.stream().findAny(x ->  ((Employee)x).getFirstName().equals(firstName)) ;



    }
}
