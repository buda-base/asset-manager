package io.bdrc.assetmanager.config;

import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.WorkTest.WorkTestParameter;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
public class ConfigServiceTests {

    @Mock
    ConfigRepository _configRepository;

    @InjectMocks
    ConfigService configService;

    List<Config> baseConfigs = new ArrayList<>();
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
            baseConfigs.add(new Config(new WorkTestLibrary(jarPath), selectedTests));
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

    @Test
    public void get_should_get_repository() {

        // Arrange
        // See @BeforeEach
        Config testReturnConfig =  baseConfigs.get(baseConfigs.size()-1);
        when(_configRepository.findById(42L)).thenReturn(Optional.of(testReturnConfig));
        // Act
        Optional<Config> foundConfig = configService.getConfigById(42L);


        // Assert
        assertTrue(foundConfig.isPresent());
        Config realConfig = foundConfig.get();
        assertSame(realConfig.getworkTestLibrary().getPath(),testReturnConfig.getworkTestLibrary().getPath());
        assertEquals(realConfig,testReturnConfig);

    }

    @Test
    public void add_should_add_repository() {
        Config testReturnConfig =  baseConfigs.get(baseConfigs.size()-1);

        when(_configRepository.findById())
        when(_configRepository.save(baseConfigs.get(0))).thenReturn(testReturnConfig);

        Config serviceConfig = configService.addConfig(baseConfigs.get(0));
        assertEquals(serviceConfig,testReturnConfig);
    }

}
