package io.bdrc.assetmanager.controllers;
import io.bdrc.assetmanager.entities.Employee;
import io.bdrc.assetmanager.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/*
This code uses Spring @RestController annotation, which marks the class as a controller where every method returns a domain object instead of a view. It is shorthand for including both
@Controller and @ResponseBody

Implicitly, this controller serves rest data when the application.properties property spring.data.rest.base-path=/api
is appended.  See io.bdrc.assetmanager.repositories
 */

@Controller
public class EmployeeServiceController {

    private final AtomicLong counter = new AtomicLong();
    //http://zetcode.com/springboot/repository/
    private  final EmployeeService  employeeService ;


    public EmployeeServiceController( EmployeeService  employeeService) {
        counter.incrementAndGet();
        System.out.println(counter);
        this.employeeService = employeeService;
    }


    //@RequestMapping("/semployee/get/{firstName}")
    @GetMapping(value = "/semployee/get/{firstName}")
    public ResponseEntity <List<Employee>> getEmployees(@PathVariable("firstName") String firstName) {
        return new ResponseEntity<>(employeeService.getbyFirstName(firstName), HttpStatus.FOUND);
    }

}
