package io.bdrc.assetmanager.WorkTest;

import io.bdrc.assetmanager.InvalidObjectData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;


@DataJpaTest
class RunnableTestParameterTest extends WorkTestTestBase {

    List<RunnableTestParameter> workTestParameterList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        BaseSetup();

        _runnableTestParameterRepository.findAll().forEach(x -> workTestParameterList.add(x));
        // get all the workTestParameters
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getName() {
        assertThat(workTestParameterList.get(0).getName().equals("name t=1 p=1)"));
        int nWorkTestParams = workTestParameterList.size();
        assertThat(workTestParameterList.get(nWorkTestParams - 1).getName().equals("name t=3 p=3)"));
    }

    @Test
    void getValue() {
        assertThat(workTestParameterList.get(0).getValue().equals("value t=1 p=1)"));
        int nWorkTestParams = workTestParameterList.size();
        assertThat(workTestParameterList.get(nWorkTestParams - 1).getValue().equals("value t=3 p=3)"));
    }

    @Test
    void getWorkTest() {
        long idFirstWork = workTestParameterList.get(0).getworkTest().getId();
        RunnableTest runnableTest = _runnableTestRepository.findById(idFirstWork).orElse(null);
        assertThatObject(runnableTest).isNotNull();

        long idFirstWtp = workTestParameterList.get(0).getId();
        RunnableTestParameter workTestParameter = _runnableTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(workTestParameter).isNotNull();
        assertThat(Objects.requireNonNull(workTestParameter).getworkTest().equals(runnableTest));

    }

    @Test
    void setName() {
        final String newName = "Test injected new name";

        long idFirstWtp = workTestParameterList.get(0).getId();
        RunnableTestParameter repoWtp = _runnableTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(repoWtp).isNotNull();
        String oldName = Objects.requireNonNull(repoWtp).getName();
        repoWtp.setName(newName);

        // Again, test the repo, just for thrills
        RunnableTestParameter oldWtp = _runnableTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(oldWtp).isNotNull();
        assertThat(oldName.equals(Objects.requireNonNull(oldWtp).getName()));

        // Update with changed name
        _runnableTestParameterRepository.save(repoWtp);
        repoWtp = _runnableTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(repoWtp).isNotNull();
        assertThat(Objects.requireNonNull(repoWtp).getName().equals(newName));
    }

    @Test
    void setValue() {
        final String newValue = "Test injected new value";

        long idFirstWtp = workTestParameterList.get(0).getId();
        RunnableTestParameter repoWtp = _runnableTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(repoWtp).isNotNull();
        String oldName = Objects.requireNonNull(repoWtp).getName();
        repoWtp.setName(newValue);

        // Again, test the repo, just for thrills
        RunnableTestParameter oldWtp = _runnableTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(oldWtp).isNotNull();
        assertThat(oldName.equals(Objects.requireNonNull(oldWtp).getName()));

        // Update with changed value
        _runnableTestParameterRepository.save(repoWtp);
        repoWtp = _runnableTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(repoWtp).isNotNull();
        assertThat(Objects.requireNonNull(repoWtp).getName().equals(newValue));
    }

    @Test
    void testEquals() {

        long idFirstWtp = workTestParameterList.get(0).getId();
        RunnableTestParameter repoWtp = _runnableTestParameterRepository.findById(idFirstWtp).orElse(null);

        assertThat(workTestParameterList.get(0).equals(repoWtp));
    }

    @Test
    void testHashCode() {
        long idFirstWtp = workTestParameterList.get(0).getId();
        RunnableTestParameter repoWtp = _runnableTestParameterRepository.findById(idFirstWtp).orElse(null);

        assertThat(workTestParameterList.get(0).equals(repoWtp));
    }
}