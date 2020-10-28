package io.bdrc.assetmanager.config;

import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.WorkTest.WorkTestParameter;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.lang.Integer.max;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConfigJpaTest  {

    @Autowired
    ConfigRepository _configRepository;

    // for convenience in fetching config ids to test
    ArrayList<Long> configIds;

    static final String workTestLibPrefix = "/Apps/testJar";

    @BeforeEach
    void setUp()  {
        for (int i = 1 ; i < 4 ; i++) {
            Set<WorkTest> workTests = TestSeries(String.format("series %d",i));

            String jarPath = String.format("%s%d.jar", workTestLibPrefix, i);
            WorkTestLibrary wtl = new WorkTestLibrary(jarPath);

            // sets all available tests
            wtl.setWorkTests(workTests);
            // set the tests you want to run, just the first and the ith
            Set<WorkTest> selectedTests = new HashSet<>();

            // https://stackoverflow.com/questions/5690351/java-stringlist-toarray-gives-classcastexception
            WorkTest[] wta =  wtl.getWorkTests().toArray(new WorkTest[workTests.size()]);
            selectedTests.add(wta[0]);
            if (i > 1) {
                selectedTests.add(wta[wta.length-1]);
            }
            this._configRepository.save(new Config(new WorkTestLibrary(jarPath),selectedTests));
        }

        configIds = new ArrayList<>((int)(_configRepository.count()));
        _configRepository.findAll().forEach( x -> configIds.add(x.getId()));
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


    @Test
    void getId() {
        Long expectedTestId = configIds.get(max(0,configIds.size()-2));
        assertThat(_configRepository.findById(expectedTestId).isPresent());
        Config testConfig = _configRepository.findById(expectedTestId).get();
        assertThat(expectedTestId.equals(testConfig.getId()));
    }


    @Test
    void get_workTestLibrary() {
        // Get a random config
        Long expectedTestId = configIds.get(0);

        Optional<Config> oc = Optional.of(_configRepository.findById(expectedTestId)
                .orElse(new Config(
                        new WorkTestLibrary("cantfindconfig"), null)));
        Config testConfig = oc.get();

        // See Setup for how first test library name is constructed
        WorkTestLibrary testWtl = testConfig.get_workTestLibrary();
        assertThat(testWtl.getPath().equals(String.format("%s%d.jar",workTestLibPrefix,1)));
    }

    @Test
    void set_workTestLibrary() {
        String expectedJarName = "Zuponga.jar";
        // Get a random config
        Long expectedTestId = configIds.get(configIds.size() - 1);

        Optional<Config> oc = Optional.of(_configRepository.findById(expectedTestId)
                .orElse(new Config(
                        new WorkTestLibrary("cantfindconfig"), null)));
        Config testConfig = oc.get();

        // See Setup for how first test library name is constructed
        Set<WorkTest>  saveWorks = testConfig.get_workTestLibrary().getWorkTests();
        WorkTestLibrary newWtl = new WorkTestLibrary("Zuponga.jar");
        newWtl.setWorkTests(saveWorks);

        Config newConfig = new Config(null,null);
        newConfig.set_workTestLibrary(newWtl);

        Config savedConfig = _configRepository.save(newConfig);

        assertThat(savedConfig.get_workTestLibrary().equals(newWtl));
    }

    @Test
    void getWorkTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("Zuponga");
        Set<WorkTest> workTests = TestSeries("Zuponga");
        Config newConfig = new Config(wtl,workTests);

        Config savedConfig = _configRepository.save(newConfig);

        assertThat(workTests.containsAll(savedConfig.getWorkTests()));
        assertThat(savedConfig.getWorkTests().containsAll(workTests));
    }

    @Test
    void setWorkTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("Zuponga");
        Set<WorkTest> workTests = TestSeries("Zuponga");
        Config newConfig = new Config(wtl,null);

        Config savedConfig = _configRepository.save(newConfig);
        savedConfig.setWorkTests(workTests);
        savedConfig = _configRepository.save(savedConfig);
        assertThat(workTests.containsAll(savedConfig.getWorkTests()));
        assertThat(savedConfig.getWorkTests().containsAll(workTests));
    }


}