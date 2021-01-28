package io.bdrc.assetmanager.config;

import io.bdrc.assetmanager.WorkTest.RunnableTest;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@DataJpaTest
class ConfigClassTest extends ConfigTestBase  {

    @Autowired
    ConfigRepository _configRepository;

    @BeforeEach
    void setUp()  {
        BaseSetup(_configRepository);
    }



    @AfterEach
    void tearDownTest() {
        _configRepository.deleteAll();
    }

    @Test
    void getId() {
        final Long setId = 42L;
        Config config = new Config();
        config.setId(setId);
        assertThat(config.getId().equals(setId));

//        _configRepository.save(new Config());
//
//        List<Config> configs = (List<Config>) _configRepository.findAll();
//        configs.forEach(x -> assertThat(x.getId() > 0));
    }

    @Test
    void setId() {
        final Long setId = 42L;
        Config config = new Config();
        config.setId(setId);
        assertThat(config.getId().equals(setId));
    }

    @Test
    void get_workTestLibrary() {
        // Get a random config
        String expectedJarName = "Zuponga.jar";
        WorkTestLibrary wtl = new WorkTestLibrary(expectedJarName);
        Config newConfig = new Config(wtl,new HashSet<>());
        assertThat(wtl.equals(newConfig.getWorkTestLibrary()));
    }

    @Test
    void set_workTestLibrary() {
        String expectedJarName = "Zuponga.jar";
        WorkTestLibrary wtl = new WorkTestLibrary(expectedJarName);
        Config newConfig = new Config(wtl, new HashSet<>());
        newConfig.setWorkTestLibrary(wtl);
        assertThat(wtl.equals(newConfig.getWorkTestLibrary()));
    }

    @Test
    void ConstructorBuildsSelectedTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("ConstructorBuildsSelectedTestsTest");
        Set<SelectedTest> runnableTests = SelectedTestSeries("ConstructorBuildsSelectedTestsTest");
        Config config = new Config(wtl, runnableTests);

        assertThat(runnableTests.containsAll(config.getSelectedTests()));
        assertThat(config.getSelectedTests().containsAll(runnableTests));
    }

    @Test
    void setWorkTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("setWorkTestsTest");
        Set<RunnableTest> runnableTests = RunnableTestSeries("setWorkTestsTest");
        Config config = new Config(wtl,new HashSet<>());

        Set<SelectedTest> selectedTests = SelectedTestSeries("setWorkTestsTest");
        config.setSelectedTests(selectedTests);
        assertThat(selectedTests.containsAll(config.getSelectedTests()));
        assertThat(config.getSelectedTests().containsAll(runnableTests));
    }

    @Test
    void testEquals() {
        Config c1 = new Config(new WorkTestLibrary("Zuponga"),SelectedTestSeries("Zoo_tests"));
        Config c2 = new Config(c1);

        assertEquals(c1,c2);
    }

    @Test
    void testHashCode() {
        Config c1 = new Config(new WorkTestLibrary("Zuponga"),SelectedTestSeries("Zoo_tests"));
        Config c2 = new Config(new WorkTestLibrary("Zuponga"),SelectedTestSeries("Zoo_tests"));
        assertThat(c2.hashCode() == c1.hashCode());
    }
}