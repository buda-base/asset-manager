package io.bdrc.assetmanager.employee;

import java.util.List;

// See http://zetcode.com/springboot/repository/ for using a repository with a service
public interface IEmployeeService {

     void addEmployee(String firstName, String lastName, String position) ;


     List<Employee> findByFirstName(String firstName);
}
