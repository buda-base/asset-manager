package io.bdrc.assetmanager.WorkTest;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class WorkTestTestBase {


    // Learned:  making these static so that you can use the @BeforeEach as a @BeforeAll
    // will fail their autoWires
    @Autowired
    protected WorkTestRepository workTestRepository;

    @Autowired
    protected WorkTestParameterRepository workTestParameterRepository;

    protected void BaseSetup() {
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

}