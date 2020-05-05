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
        this.repository.save(new Employee("Bilbo", "Baggins", "burglar"));
        this.repository.save(new Employee("BigData", "Warbucks", "capitalist"));
        this.repository.save(new Employee("Annie", "Dailey", "fictionist"));
        this.repository.save(new Employee("William", "Bailey", "wanderer"));
        this.repository.save(new Employee("Imelda", "Baggins", "shoe stringer"));
        this.repository.save(new Employee("Imelda", "Baggins", "shoe stringer"));
        this.repository.save(new Employee("Fernando", "Alonso", "pilote"));
        this.repository.save(new Employee("Alonso", "Mosley", "FBI"));
        this.repository.save(new Employee("Charles", "Grodin", "white collar criminal"));
    }
}