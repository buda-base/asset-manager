package io.bdrc.assetmanager.components;

import io.bdrc.assetmanager.WorkTest.RunnableTest;
import io.bdrc.assetmanager.WorkTest.RunnableTestParameter;
import io.bdrc.assetmanager.WorkTest.RunnableTestRepository;
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
    public ConfigLoader(ConfigRepository repository, final RunnableTestRepository _runnableTestRepository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {
        for (int i = 1 ; i < 4 ; i++) {
            Set<RunnableTest> runnableTests = TestSeries(String.format("series %d",i));

            String jarPath = String.format("/Apps/testJar%d.jar",i);
            WorkTestLibrary wtl = new WorkTestLibrary(jarPath);

            // sets all available tests
            wtl.setRunnableTests(runnableTests);
            // set the tests you want to run, just the first and the ith
            Set<RunnableTest> selectedTests = new HashSet<>();
            RunnableTest[] wta = wtl.getRunnableTests().toArray(new RunnableTest[runnableTests.size()]);
            selectedTests.add(wta[0]);
            if (i > 1) {
                selectedTests.add(wta[wta.length-1]);
            }

            // why dd I do this?
            // repository.save(new Config(new WorkTestLibrary(jarPath),selectedTests));
            repository.save(new Config(wtl,selectedTests));
        }
    }

    Set<RunnableTest> TestSeries(String discriminator) {
        HashSet<RunnableTest> runnableTests = new HashSet<>();

        for (int i = 1; i < 4; i++) {
            RunnableTest runnableTest = new RunnableTest(String.format("WorkTestName%s", i));
            for (int j = 1; j <= i; j++) {
                new RunnableTestParameter(String.format("%s t=%s p=%s", discriminator, i, j),
                        String.format("value t=%s p=%s", i, j), runnableTest);
            }
            runnableTests.add(runnableTest);
        }
        return runnableTests;
    }
}