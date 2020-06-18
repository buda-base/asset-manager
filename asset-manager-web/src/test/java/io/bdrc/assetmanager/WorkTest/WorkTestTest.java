package io.bdrc.assetmanager.WorkTest;

import io.bdrc.assetmanager.InvalidObjectData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WorkTestTest extends WorkTestTestBase {


    List<WorkTest> workTestList = new ArrayList<>();

    @BeforeEach
    void setUp() throws InvalidObjectData {
        BaseSetup();
        workTestRepository.findAll().forEach(x -> workTestList.add(x));
        // get all the workTestParameters
    }

    @Test
    void workRepositoryTest() {
        List<WorkTest> workTests = (List<WorkTest>) workTestRepository.findAll();
        assertThat(workTests.size() == 3);

    }

    // This just tests that I've written the classes right.
    @Test
    void workRepositoryTestWorkParametersWorkTest() {
        //List<WorkTest> workTests = (List<WorkTest>)
        workTestRepository.findAll().forEach((WorkTest x) ->
        workTestParameterRepository.findByWorkTest(x)
                .forEach(wtp -> assertThat(wtp.getWorkTest().equals(x))));
    }

    @Test
    void addWorkTestParameter() throws InvalidObjectData {
        WorkTest wt = workTestList.get(0);
        Set<WorkTestParameter> w_forWtp = wt.getWorkTestParameters();
        int preWtps = w_forWtp.size();
        new WorkTestParameter("wtpnew", "wtpnewValue", wt);
        int postWtps = wt.getWorkTestParameters().size();
        Set<WorkTestParameter> w_forWtpPost = wt.getWorkTestParameters();
        assertThat(w_forWtpPost.size() ==  postWtps );
        assertThat(postWtps ==  1 + preWtps );
    }

    @Test
    void getWorkTestParametersCounts() {
        int expectedParamCount = 0;
        for (final WorkTest workTest : workTestList) {
            assertThat(workTest.getWorkTestParameters().size() == ++expectedParamCount);
        }
    }

    @Test
    void setWorkTestParameters() throws InvalidObjectData {
        WorkTest wt = new WorkTest("wtpName");
        Set<WorkTestParameter> newWtps = newWorkTestParametersWithoutWorks();

        // test that the parent object was added
        wt.setWorkTestParameters(newWtps);
        wt.getWorkTestParameters().forEach(x -> assertThat(x.getWorkTest().equals(wt)));

        // Test that the original was modified in place
        Set<WorkTestParameter> addedWtps = wt.getWorkTestParameters();
        assertThat(addedWtps.equals(newWtps));
    }

    // Tests that you cannot add duplicate workTest Parameter names to a WorkTest
    @Test
    void uniqueWorkTestReplaceParameters() {
        WorkTest wt = new WorkTest("wtpName");
        Set<WorkTestParameter> newWtps = new HashSet<>();
        newWtps.add( new WorkTestParameter("wtp1name","wtp1value"));
        newWtps.add( new WorkTestParameter("wtp1name","wtp2value"));

        Exception exception = assertThrows(InvalidObjectData.class, () -> wt.setWorkTestParameters(newWtps));

        // Test the expected exception was thrown
        String expectedMessage = "WorkTestParameter Collection has duplicate elements";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }
    @Test
    void getId() {
    }

    @Test
    void setId() {
    }

    @Test
    void getTestName() {
        final String testName = "TestName" ;
        WorkTest workTest = new WorkTest(testName);
        assertEquals(workTest.getTestName(),testName);
    }

    @Test
    void setTestName() {
        final String testName = "TestName" ;
        final String newTestName = "newTestName";
        WorkTest workTest = new WorkTest(testName);
        workTest.setTestName(newTestName );
        assertEquals(workTest.getTestName(),newTestName);
    }

    @Test
    void testEquals()  throws InvalidObjectData {
        final String oldName = "oldWorkTestName";

        WorkTest wtOld = new WorkTest(oldName);
        Set<WorkTestParameter> origParams = newWorkTestParametersWithoutWorks();
        wtOld.setWorkTestParameters(origParams);

        WorkTest wtNew = new WorkTest(wtOld);

        assertEquals(wtNew,wtOld);
    }

    @Test
    void testHashCode() {
    }
}