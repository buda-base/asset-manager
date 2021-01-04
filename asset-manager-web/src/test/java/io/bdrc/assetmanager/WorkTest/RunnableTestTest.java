package io.bdrc.assetmanager.WorkTest;

import io.bdrc.assetmanager.InvalidObjectData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RunnableTestTest extends WorkTestTestBase {


    List<RunnableTest> _runnableTestList = new ArrayList<>();

    @BeforeEach
    void setUp()  {
        BaseSetup();
        _runnableTestRepository.findAll().forEach(x -> _runnableTestList.add(x));
        // get all the workTestParameters
    }

    @Test
    void workRepositoryTest() {
        List<RunnableTest> runnableTests = (List<RunnableTest>) _runnableTestRepository.findAll();
        assertThat(runnableTests.size() == 3);

    }

    // This just tests that I've written the classes right.
//    @Test
//    void workRepositoryTestWorkParametersWorkTest() {
//        //List<WorkTest> workTests = (List<WorkTest>)
//        _runnableTestRepository.findAll().forEach((RunnableTest x) ->
//                _runnableTestParameterRepository.findByRunnableTest(x)
//                        .forEach(wtp -> assertThat(wtp.getworkTest().equals(x))));
//    }

    @Test
    void addWorkTestParameter() {
        RunnableTest wt = _runnableTestList.get(0);
        Set<RunnableTestParameter> w_forWtp = wt.getworkTestParameters();
        int preWtps = w_forWtp.size();
        new RunnableTestParameter("wtpnew", "wtpnewValue", wt);
        int postWtps = wt.getworkTestParameters().size();
        Set<RunnableTestParameter> w_forWtpPost = wt.getworkTestParameters();
        assertThat(w_forWtpPost.size() == postWtps);
        assertThat(postWtps == 1 + preWtps);
    }

    @Test
    void getWorkTestParametersCounts() {
        int expectedParamCount = 0;
        for (final RunnableTest runnableTest : _runnableTestList) {
            assertThat(runnableTest.getworkTestParameters().size() == ++expectedParamCount);
        }
    }

    @Test
    void setWorkTestParameters() throws InvalidObjectData {
        RunnableTest wt = new RunnableTest("wtpName");
        Set<RunnableTestParameter> newWtps = newWorkTestParametersWithoutWorks();

        // test that the parent object was added
        wt.setworkTestParameters(newWtps);
        wt.getworkTestParameters().forEach(x -> assertThat(x.getworkTest().equals(wt)));

        // Test that the original was modified in place
        Set<RunnableTestParameter> addedWtps = wt.getworkTestParameters();
        assertThat(addedWtps.equals(newWtps));
    }

    // Tests that you cannot add duplicate workTest Parameter names to a WorkTest
    @Test
    void uniqueWorkTestReplaceParameters() {
        RunnableTest wt = new RunnableTest("wtpName");
        Set<RunnableTestParameter> newWtps = new HashSet<>();
        newWtps.add(new RunnableTestParameter("wtp1name", "wtp1value"));
        newWtps.add(new RunnableTestParameter("wtp1name", "wtp2value"));

        Exception exception = assertThrows(InvalidObjectData.class, () -> wt.setworkTestParameters(newWtps));

        // Test the expected exception was thrown
        String expectedMessage = "RunnableTestParameter Collection has duplicate elements";
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
        final String testName = "TestName";
        RunnableTest runnableTest = new RunnableTest(testName);
        assertEquals(runnableTest.getTestName(), testName);
    }

    @Test
    void setTestName() {
        final String testName = "TestName";
        final String newTestName = "newTestName";
        RunnableTest runnableTest = new RunnableTest(testName);
        runnableTest.setTestName(newTestName);
        assertEquals(runnableTest.getTestName(), newTestName);
    }

    @Test
    void testEquals() throws InvalidObjectData {
        final String oldName = "oldWorkTestName";

        RunnableTest wtOld = new RunnableTest(oldName);
        Set<RunnableTestParameter> origParams = newWorkTestParametersWithoutWorks();
        wtOld.setworkTestParameters(origParams);

        RunnableTest wtNew = new RunnableTest(wtOld);

        assertEquals(wtNew, wtOld);
    }

    @Test
    void testHashCode() {
    }

    @Test
    void replaceWorkTestParameter() throws InvalidObjectData {

        // Arrange
        final String oldName = "oldWorkTestName";
        final String newValue = new UUID(8,8).toString();

        RunnableTest wtOld = new RunnableTest(oldName);
        Set<RunnableTestParameter> origParams = newWorkTestParametersWithoutWorks();
        wtOld.setworkTestParameters(origParams);

        int nParamsPre = wtOld.getworkTestParameters().size();

        RunnableTestParameter newWtp = null;
        for (RunnableTestParameter wtp : origParams) {
            newWtp = new RunnableTestParameter(wtp);
            break;
        }

        //
        assert newWtp != null;
        newWtp.setValue(newValue);

        // Act
        // Should replace the existing newWtp.getName()
        wtOld.replaceWorkTestParameter(newWtp);

        // Assert
        assertEquals(nParamsPre, wtOld.getworkTestParameters().size());

        for (RunnableTestParameter wtp :  wtOld.getworkTestParameters()) {
            if (wtp.getName().equals(newWtp.getName())) {
                assertEquals(newValue,wtp.getValue());
                break;
            }
            else {
                // We're kinda testing set here
                assertNotEquals(newValue,wtp.getValue());
            }
        }
    }

    @Test
    void removeWorkTestParameter() {
    }
}