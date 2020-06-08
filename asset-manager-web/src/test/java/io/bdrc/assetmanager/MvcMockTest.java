package io.bdrc.assetmanager;


import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.bdrc.assetmanager.controllers.GreetingController;
import io.bdrc.assetmanager.entities.Employee;
import io.bdrc.assetmanager.services.EmployeeService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@WebMvcTest(GreetingController.class)
public class MvcMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Test
    public void webShouldReturnListFromService() throws Exception {

        List<Employee> mockGet = Arrays.asList(
                new Employee("Ralph", "Kramden", "Bus spotter"),
                new Employee("Ed", "Norton", "Eer do well"));

        new AtomicReference<>(when(service.getbyFirstName("World")).thenReturn(mockGet));
        this.mockMvc.perform(get("/semployee/get/World")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, Mock")));
    }
}