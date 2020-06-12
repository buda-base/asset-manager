package io.bdrc.assetmanager.WorkTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WorkTestTest extends WorkTestTestBase {

    @BeforeEach
    void setup() {
        BaseSetup();
    }
    @Test
    void workRepositoryTest() {
        List<WorkTest> workTests = (List<WorkTest>) workTestRepository.findAll();
        assertThat(workTests.size() == 3);

    }

    // This just tests that I've written the classes right.
    @Test
    void workRepositoryTestWorkParametersWorkTest() {
        List<WorkTest> workTests = (List<WorkTest>) workTestRepository.findAll();

        workTests.forEach((WorkTest x) -> workTestParameterRepository.findByWorkTest(x)
                .forEach(wtp -> assertThat(wtp.getWorkTest().equals(x))));
    }


}