package io.bdrc.assetmanager.config;

import io.bdrc.assetmanager.WorkTest.RunnableTest;
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
import static org.junit.Assert.assertNotEquals;

@DataJpaTest
class ConfigRepositoryTest extends ConfigTestBase {

    @Autowired
    ConfigRepository _configRepository;

    // for convenience in fetching config ids to test
    ArrayList<Long> configIds;

    static final String workTestLibPrefix = "/Apps/testJar";

    @BeforeEach
    void setUp()  {
        BaseSetup(_configRepository);
        configIds = new ArrayList<>((int)(_configRepository.count()));
        _configRepository.findAll().forEach( x -> configIds.add(x.getId()));
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
        WorkTestLibrary testWtl = testConfig.getWorkTestLibrary();
        assertThat(testWtl.getPath().equals(String.format("%s%d.jar",workTestLibPrefix,1)));
    }

    @Test
    void set_workTestLibrary() {
        String expectedJarName = "Zuponga.jar";
        // Get a random config
        Long expectedConfigId = configIds.get(configIds.size() - 1);

        Optional<Config> oc = Optional.of(_configRepository.findById(expectedConfigId)
                .orElse(new Config(
                        new WorkTestLibrary("cant-find-config"), new HashSet<>())));
        Config testConfig = oc.get();

        // See Setup for how first test library name is constructed
        Set<RunnableTest>  saveWorks = testConfig.getWorkTestLibrary().getRunnableTests();
        WorkTestLibrary newWtl = new WorkTestLibrary(expectedJarName);
        newWtl.setRunnableTests(saveWorks);

        // Create an empty test library in the config
        Config newConfig = new Config(new WorkTestLibrary("no-path"),new HashSet<>());

        assertNotEquals(newConfig.getWorkTestLibrary(), newWtl);

        // change it to a known library
        newConfig.setWorkTestLibrary(newWtl);

        Config savedConfig = _configRepository.save(newConfig);

        assertThat(savedConfig.getWorkTestLibrary().equals(newWtl));
    }

    @Test
    void getWorkTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("getWorkTestsTest");
        Set<SelectedTest> selectedTests = SelectedTestSeries("getWorkTestsTest");
        Config newConfig = new Config(wtl, selectedTests);

        Config savedConfig = _configRepository.save(newConfig);

        assertThat(selectedTests.containsAll(savedConfig.getSelectedTests()));
        assertThat(savedConfig.getSelectedTests().containsAll(selectedTests));
    }

    @Test
    void setSelectedTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("setSelectedTestsTest");
        Set<SelectedTest> selectedTests = SelectedTestSeries("setSelectedTestsTest");
        Config newConfig = new Config(wtl, selectedTests);
        Config savedConfig = _configRepository.save(newConfig);

        Set<SelectedTest> expectedTests = SelectedTestSeries("expectedSelectedTestsTest");

        savedConfig.setSelectedTests(expectedTests);
        savedConfig = _configRepository.save(savedConfig);
        assertThat(expectedTests.containsAll(savedConfig.getSelectedTests()));
        assertThat(savedConfig.getSelectedTests().containsAll(expectedTests));
    }
}