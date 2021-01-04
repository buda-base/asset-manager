package io.bdrc.assetmanager.config;

import io.bdrc.assetmanager.WorkTest.RunnableTest;
import io.bdrc.assetmanager.WorkTest.RunnableTestParameter;
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
class ConfigRepositoryTest {

    @Autowired
    ConfigRepository _configRepository;

    // for convenience in fetching config ids to test
    ArrayList<Long> configIds;

    static final String workTestLibPrefix = "/Apps/testJar";

    @BeforeEach
    void setUp()  {
        for (int i = 1 ; i < 4 ; i++) {
            Set<RunnableTest> runnableTests = TestSeries(String.format("series %d",i));

            String jarPath = String.format("%s%d.jar", workTestLibPrefix, i);
            WorkTestLibrary wtl = new WorkTestLibrary(jarPath);

            // sets all available tests
            wtl.setRunnableTests(runnableTests);
            // set the tests you want to run, just the first and the ith
            Set<RunnableTest> selectedTests = new HashSet<>();

            // https://stackoverflow.com/questions/5690351/java-stringlist-toarray-gives-classcastexception
            RunnableTest[] wta =  wtl.getRunnableTests().toArray(new RunnableTest[runnableTests.size()]);
            selectedTests.add(wta[0]);
            if (i > 1) {
                selectedTests.add(wta[wta.length-1]);
            }
            this._configRepository.save(new Config(new WorkTestLibrary(jarPath),selectedTests));
        }

        configIds = new ArrayList<>((int)(_configRepository.count()));
        _configRepository.findAll().forEach( x -> configIds.add(x.getId()));
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
                        new WorkTestLibrary("cant-find-config"), new HashSet<>())));
        Config testConfig = oc.get();

        // See Setup for how first test library name is constructed
        WorkTestLibrary testWtl = testConfig.getworkTestLibrary();
        assertThat(testWtl.getPath().equals(String.format("%s%d.jar",workTestLibPrefix,1)));
    }

    @Test
    void set_workTestLibrary() {
        String expectedJarName = "Zuponga.jar";
        // Get a random config
        Long expectedTestId = configIds.get(configIds.size() - 1);

        Optional<Config> oc = Optional.of(_configRepository.findById(expectedTestId)
                .orElse(new Config(
                        new WorkTestLibrary("cant-find-config"), new HashSet<>())));
        Config testConfig = oc.get();

        // See Setup for how first test library name is constructed
        Set<RunnableTest>  saveWorks = testConfig.getworkTestLibrary().getRunnableTests();
        WorkTestLibrary newWtl = new WorkTestLibrary(expectedJarName);
        newWtl.setRunnableTests(saveWorks);

        Config newConfig = new Config(new WorkTestLibrary("no-path"),new HashSet<>());
        newConfig.setworkTestLibrary(newWtl);

        Config savedConfig = _configRepository.save(newConfig);

        assertThat(savedConfig.getworkTestLibrary().equals(newWtl));
    }

    @Test
    void getWorkTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("Zuponga");
        Set<RunnableTest> runnableTests = TestSeries("Zuponga");
        Config newConfig = new Config(wtl, runnableTests);

        Config savedConfig = _configRepository.save(newConfig);

        assertThat(runnableTests.containsAll(savedConfig.getRunnableTests()));
        assertThat(savedConfig.getRunnableTests().containsAll(runnableTests));
    }

    @Test
    void setWorkTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("Zuponga");
        Set<RunnableTest> runnableTests = TestSeries("Zuponga");
        Config newConfig = new Config(wtl,new HashSet<>());

        Config savedConfig = _configRepository.save(newConfig);
        savedConfig.setRunnableTests(runnableTests);
        savedConfig = _configRepository.save(savedConfig);
        assertThat(runnableTests.containsAll(savedConfig.getRunnableTests()));
        assertThat(savedConfig.getRunnableTests().containsAll(runnableTests));
    }


}