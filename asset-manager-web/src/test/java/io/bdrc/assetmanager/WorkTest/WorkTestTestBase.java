package io.bdrc.assetmanager.WorkTest;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class WorkTestTestBase {


    // Learned:  making these static so that you can use the @BeforeEach as a @BeforeAll
    // will fail their autoWires
    @Autowired
    protected RunnableTestRepository _runnableTestRepository;

    @Autowired
    protected RunnableTestParameterRepository _runnableTestParameterRepository;

    protected void BaseSetup() {
        List<RunnableTest> runnableTests = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            RunnableTest runnableTest = new RunnableTest(String.format("WorkTestName%s", i));
            for (int j = 1; j <= i; j++) {
                new RunnableTestParameter(String.format("name t=%s p=%s", i, j),
                        String.format("value t=%s p=%s", i, j), runnableTest);
            }
            runnableTests.add(runnableTest);
        }
        _runnableTestRepository.saveAll(runnableTests);

    }

    protected Set<RunnableTestParameter> newWorkTestParametersWithoutWorks() {
        Set<RunnableTestParameter> workTestParameterHashSet = new HashSet<>();
        workTestParameterHashSet.add(new RunnableTestParameter("wtp1name", "wtp1value"));
        workTestParameterHashSet.add(new RunnableTestParameter("wtp2name", "wtp2value"));
        workTestParameterHashSet.add(new RunnableTestParameter("wtp3name", "wtp3value"));

        return workTestParameterHashSet;

    }

}