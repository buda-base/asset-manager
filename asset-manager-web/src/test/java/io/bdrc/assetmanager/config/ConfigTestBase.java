package io.bdrc.assetmanager.config;

import io.bdrc.assetmanager.WorkTest.RunnableTest;
import io.bdrc.assetmanager.WorkTest.RunnableTestParameter;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;

import java.util.HashSet;
import java.util.Set;

public class ConfigTestBase {
    protected void BaseSetup(ConfigRepository configRepository) {
        for (int i = 1; i < 4; i++) {
            Set<RunnableTest> workTests = RunnableTestSeries(String.format("series %d", i));

            String jarPath = String.format("/Apps/testJar%d.jar", i);
            WorkTestLibrary wtl = new WorkTestLibrary(jarPath);

            // sets all available tests
            wtl.setRunnableTests(workTests);
            // set the tests you want to run, just the first and the ith
            Set<SelectedTest> selectedTests = new HashSet<>();

            // https://stackoverflow.com/questions/5690351/java-stringlist-toarray-gives-classcastexception
            RunnableTest[] wta = wtl.getRunnableTests().toArray(new RunnableTest[workTests.size()]);
            SelectedTest st = SelectedTest.fromRunnable(wta[0]);
            selectedTests.add(st);
            if (i > 1) {
                selectedTests.add(SelectedTest.fromRunnable(wta[wta.length - 1]));
            }
            configRepository.save(new Config(new WorkTestLibrary(jarPath), selectedTests));
        }
    }
    protected Set<RunnableTest> RunnableTestSeries(String discriminator) {
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


    protected Set<SelectedTest> SelectedTestSeries(String discriminator) {
        HashSet<SelectedTest> selectedTests = new HashSet<>();

        for (int i = 1; i < 4; i++) {
            SelectedTest selectedTest = new SelectedTest();
            for (int j = 1; j <= i; j++) {
                new SelectedTestParameter(String.format("name %s =%s", discriminator, i),
                        String.format("value = %d", j),selectedTest);
            }
            selectedTests.add(selectedTest);
        }
        return selectedTests;
    }
}
