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
class WorkTestParameterTest extends WorkTestTestBase {

    List<WorkTestParameter> workTestParameterList = new ArrayList<>();

    @BeforeEach
    void setUp() throws InvalidObjectData {
        BaseSetup();

        workTestParameterRepository.findAll().forEach(x -> workTestParameterList.add(x));
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
        long idFirstWork = workTestParameterList.get(0).getWorkTest().getId();
        WorkTest workTest = workTestRepository.findById(idFirstWork).orElse(null);
        assertThatObject(workTest).isNotNull();

        long idFirstWtp = workTestParameterList.get(0).getId();
        WorkTestParameter workTestParameter = workTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(workTestParameter).isNotNull();
        assertThat(Objects.requireNonNull(workTestParameter).getWorkTest().equals(workTest));

    }

    @Test
    void setName() {
        final String newName = "Test injected new name";

        long idFirstWtp = workTestParameterList.get(0).getId();
        WorkTestParameter repoWtp = workTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(repoWtp).isNotNull();
        String oldName = Objects.requireNonNull(repoWtp).getName();
        repoWtp.setName(newName);

        // Again, test the repo, just for thrills
        WorkTestParameter oldWtp = workTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(oldWtp).isNotNull();
        assertThat(oldName.equals(Objects.requireNonNull(oldWtp).getName()));

        // Update with changed name
        workTestParameterRepository.save(repoWtp);
        repoWtp = workTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(repoWtp).isNotNull();
        assertThat(Objects.requireNonNull(repoWtp).getName().equals(newName));
    }

    @Test
    void setValue() {
        final String newValue = "Test injected new value";

        long idFirstWtp = workTestParameterList.get(0).getId();
        WorkTestParameter repoWtp = workTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(repoWtp).isNotNull();
        String oldName = Objects.requireNonNull(repoWtp).getName();
        repoWtp.setName(newValue);

        // Again, test the repo, just for thrills
        WorkTestParameter oldWtp = workTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(oldWtp).isNotNull();
        assertThat(oldName.equals(Objects.requireNonNull(oldWtp).getName()));

        // Update with changed value
        workTestParameterRepository.save(repoWtp);
        repoWtp = workTestParameterRepository.findById(idFirstWtp).orElse(null);
        assertThatObject(repoWtp).isNotNull();
        assertThat(Objects.requireNonNull(repoWtp).getName().equals(newValue));
    }

    @Test
    void testEquals() {

        long idFirstWtp = workTestParameterList.get(0).getId();
        WorkTestParameter repoWtp = workTestParameterRepository.findById(idFirstWtp).orElse(null);

        assertThat(workTestParameterList.get(0).equals(repoWtp));
    }

    @Test
    void testHashCode() {
        long idFirstWtp = workTestParameterList.get(0).getId();
        WorkTestParameter repoWtp = workTestParameterRepository.findById(idFirstWtp).orElse(null);

        assertThat(workTestParameterList.get(0).equals(repoWtp));
    }
}