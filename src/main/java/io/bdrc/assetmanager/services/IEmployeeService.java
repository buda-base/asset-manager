package io.bdrc.assetmanager.services;

import io.bdrc.assetmanager.entities.Employee;

import java.util.List;

// See http://zetcode.com/springboot/repository/ for using a repository with a service
public interface IEmployeeService {
     void addEmployee(String firstName, String lastName, String position) ;

     List<Employee> getbyFirstName(String firstName);
}
