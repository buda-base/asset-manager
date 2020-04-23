package io.bdrc.assetmanager.components;

import io.bdrc.assetmanager.entities.Employee;
import io.bdrc.assetmanager.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// https://spring.io/guides/tutorials/react-and-spring-data-rest/
@Component
public class DatabaseLoader implements CommandLineRunner {

    private final EmployeeRepository repository;

    @Autowired
    public DatabaseLoader(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {

        // Maybe you do connects here?
        this.repository.save(new Employee("Frodo", "Baggins", "ring bearer"));
    }
}