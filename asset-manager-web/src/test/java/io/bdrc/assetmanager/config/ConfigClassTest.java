package io.bdrc.assetmanager.config;

import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.WorkTest.WorkTestParameter;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest
class ConfigClassTest  {

    /*
    @Autowired ConfigRepository _configRepository;

    @BeforeEach
    void setUp()  {
        for (int i = 1 ; i < 4 ; i++) {
            Set<WorkTest> workTests = TestSeries(String.format("series %d",i));

            String jarPath = String.format("/Apps/testJar%d.jar",i);
            WorkTestLibrary wtl = new WorkTestLibrary(jarPath);

            // sets all available tests
            wtl.setWorkTests(workTests);
            // set the tests you want to run, just the first and the ith
            Set<WorkTest> selectedTests = new HashSet<>();

            // https://stackoverflow.com/questions/5690351/java-stringlist-toarray-gives-classcastexception
            WorkTest[] wta = (WorkTest[]) wtl.getWorkTests().toArray(new WorkTest[workTests.size()]);
            selectedTests.add(wta[0]);
            if (i > 1) {
                selectedTests.add(wta[wta.length-1]);
            }
            this._configRepository.save(new Config(new WorkTestLibrary(jarPath),selectedTests));
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

    @AfterEach
    void tearDownTest() {
        _configRepository.deleteAll();
    }

     */

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
        assertThat(wtl.equals(newConfig.get_workTestLibrary()));
    }

    @Test
    void set_workTestLibrary() {
        String expectedJarName = "Zuponga.jar";
        WorkTestLibrary wtl = new WorkTestLibrary(expectedJarName);
        Config newConfig = new Config(wtl, new HashSet<>());
        newConfig.set_workTestLibrary(wtl);
        assertThat(wtl.equals(newConfig.get_workTestLibrary()));
    }

    @Test
    void getWorkTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("Zuponga");
        Set<WorkTest> workTests = TestSeries("Zuponga");
        Config config = new Config(wtl,workTests);

        assertThat(workTests.containsAll(config.getWorkTests()));
        assertThat(config.getWorkTests().containsAll(workTests));
    }

    @Test
    void setWorkTests() {
        WorkTestLibrary wtl = new WorkTestLibrary("Zuponga");
        Set<WorkTest> workTests = TestSeries("Zuponga");
        Config config = new Config(wtl,new HashSet<>());
        config.setWorkTests(workTests);
        assertThat(workTests.containsAll(config.getWorkTests()));
        assertThat(config.getWorkTests().containsAll(workTests));
    }

    @Test
    void testEquals() {
        Config c1 = new Config(new WorkTestLibrary("Zuponga"),TestSeries("Zoo_tests"));
        Config c2 = new Config(c1);

        assertThat(c1.equals(c2));
        assertThat(c2.equals(c1));

    }

    @Test
    void testHashCode() {
        Config c1 = new Config(new WorkTestLibrary("Zuponga"),TestSeries("Zoo_tests"));
        Config c2 = new Config(new WorkTestLibrary("Zuponga"),TestSeries("Zoo_tests"));
        assertThat(c2.hashCode() == c1.hashCode());
    }
}