package io.bdrc.assetmanager.components;

import io.bdrc.assetmanager.config.Config;
import io.bdrc.assetmanager.config.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// https://spring.io/guides/tutorials/react-and-spring-data-rest/
@Component
public class ConfigLoader implements CommandLineRunner {

    private final ConfigRepository repository;

    @Autowired
    public ConfigLoader(ConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {

        // Maybe you do connects here?
        this.repository.save(new Config("/Apps/testJar.jar", workTestSet));
        this.repository.save(new Config("/Apps/testJar.jar", workTestSet));
        this.repository.save(new Config("/Apps/testJar2.jar", workTestSet));
        this.repository.save(new Config("/Apps/testJar2.jar", workTestSet));
        this.repository.save(new Config("/Apps/testJar3.jar", workTestSet));
    }
}