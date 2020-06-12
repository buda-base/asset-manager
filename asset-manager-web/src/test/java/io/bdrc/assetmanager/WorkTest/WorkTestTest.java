package io.bdrc.assetmanager.WorkTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WorkTestTest {

    // Learned:  making these static so that you can use the @BeforeEach as a @BeforeAll
    // will fail their autoWires
    @Autowired
    private WorkTestRepository workTestRepository;

    @Autowired
    private WorkTestParameterRepository workTestParameterRepository;

    @BeforeEach
    void setUp() {
        List<WorkTest> workTests = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            WorkTest workTest = new WorkTest(String.format("WorkTestName%s", i));
            for (int j = 1; j <= i; j++) {
                WorkTestParameter wtp = new WorkTestParameter(String.format("name t=%s p=%s", i, j),
                        String.format("value t=%s p=%s", i, j), workTest);
            }
            workTests.add(workTest);
        }
        workTestRepository.saveAll(workTests);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void workRepositoryTest() {
        List<WorkTest> workTests = (List<WorkTest>) workTestRepository.findAll();
        assertThat(workTests.size() == 3);

    }

    // This just tests that I've written the classes right.
    @Test
    void workRepositoryTestWorkParameters() {
        List<WorkTest> workTests = (List<WorkTest>) workTestRepository.findAll();

        workTests.forEach(x -> workTestParameterRepository.findByWorkTest(x)
                .forEach(wtp -> assertThat(wtp.getWorkTest().equals(x))));
    }

    @Test
    void setWorkTestParameters() {
    }

    @Test
    void getId() {
    }

    @Test
    void setId() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }
}