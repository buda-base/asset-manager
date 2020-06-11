package io.bdrc.assetmanager.employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


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
