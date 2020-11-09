package io.bdrc.assetmanager.employee;

import java.util.List;

public interface IEmployeeService {

     void addEmployee(String firstName, String lastName, String position) ;


     List<Employee> findByFirstName(String firstName);
}
