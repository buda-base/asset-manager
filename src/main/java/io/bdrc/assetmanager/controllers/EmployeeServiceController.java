package io.bdrc.assetmanager.controllers;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.bdrc.assetmanager.entities.Employee;
import io.bdrc.assetmanager.models.GreetingModel;
import io.bdrc.assetmanager.repositories.EmployeeRepository;
import io.bdrc.assetmanager.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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


    @RequestMapping("/semployee/get/{firstName}")
    public @ResponseBody getEmployees(String firstName) {
        return employeeService.getbyFirstName(firstName);
    }

}
