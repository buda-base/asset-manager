package io.bdrc.assetmanager.components;

import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.WorkTest.WorkTestParameter;
import io.bdrc.assetmanager.WorkTest.WorkTestRepository;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;
import io.bdrc.assetmanager.config.Config;
import io.bdrc.assetmanager.config.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

// https://spring.io/guides/tutorials/react-and-spring-data-rest/
@Component
public class ConfigLoader implements CommandLineRunner {

    private final ConfigRepository repository;

    @Autowired
    public ConfigLoader(ConfigRepository repository, final WorkTestRepository _workTestRepository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {

        for (int i = 1 ; i < 4 ; i++) {
            Set<WorkTest> workTests = TestSeries(String.format("series %d",i));

            String jarPath = String.format("/Apps/testJar%d.jar",i);
            WorkTestLibrary wtl = new WorkTestLibrary(jarPath);

            // sets all available tests
            wtl.setWorkTests(workTests);
            // set the tests you want to run, just the first and the ith
            Set<WorkTest> selectedTests = new HashSet<>();
            WorkTest[] wta = wtl.getWorkTests().toArray(new WorkTest[workTests.size()]);
            selectedTests.add(wta[0]);
            if (i > 1) {
                selectedTests.add(wta[wta.length-1]);
            }
            repository.save(new Config(new WorkTestLibrary(jarPath),selectedTests));
        }
    }

    Set<WorkTest> TestSeries(String discriminator) {
        HashSet<WorkTest> workTests = new HashSet<>();

        for (int i = 1; i < 4; i++) {
            WorkTest workTest = new WorkTest(String.format("WorkTestName%s", i));
            for (int j = 1; j <= i; j++) {
                new WorkTestParameter(String.format("%s t=%s p=%s", discriminator, i, j),
                        String.format("value t=%s p=%s", i, j), workTest);
            }
            workTests.add(workTest);
        }
        return workTests;
    }
}