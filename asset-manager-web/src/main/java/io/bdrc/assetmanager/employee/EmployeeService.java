package io.bdrc.assetmanager.employee;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Problematic. This is a layer over the repository which doesn't really add
 * anything. Except if we need finer grained ways of getting data by some special criteria,
 * or if we need some business logic
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

    @Override
    public List<Employee> findByFirstName(final String firstName) {
        return null;
    }

    public List<Employee> getbyFirstName(String firstName)
    {
        List<Employee> employees = (List <Employee>) this.repository.findAll();
        return employees.stream()
                .filter(x -> x.getFirstName().equals(firstName))
                .collect(Collectors.toList());
    }
}
