package io.bdrc.assetmanager;


import io.bdrc.assetmanager.controllers.EmployeeServiceController;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeServiceController.class)
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

        // https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc
        // training wheels
//        String response =
//                this.mockMvc.perform(get("/semployee/get/World")).andReturn().getResponse().getContentAsString();
        this.mockMvc.perform(get("/semployee/get/World")).andDo(print()).andExpect(status().isFound())
                .andExpect(jsonPath("$[0].firstName").value(mockGet.get(0).getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value((mockGet.get(0).getLastName())))
                .andExpect(jsonPath("$[0].description").value(mockGet.get(0).getDescription()))        
                .andExpect(jsonPath("$[1].firstName").value(mockGet.get(1).getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value((mockGet.get(1).getLastName())))
                .andExpect(jsonPath("$[1].description").value(mockGet.get(1).getDescription()));
    }
}