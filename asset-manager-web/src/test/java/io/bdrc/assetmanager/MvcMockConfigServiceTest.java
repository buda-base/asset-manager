package io.bdrc.assetmanager;

import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;
import io.bdrc.assetmanager.config.Config;
import io.bdrc.assetmanager.config.ConfigService;
import io.bdrc.assetmanager.config.ConfigServiceController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import javax.management.modelmbean.ModelMBeanNotificationBroadcaster;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ConfigServiceController.class)
public class MvcMockConfigServiceTest {

    @Autowired
    MockMvc _mockMvc;

    @MockBean
    private ConfigService _configService;

    @Test
    public void webShouldReturnListFromService() throws Exception {

        List<WorkTest> workTests = Arrays.asList(
                new WorkTest("TestName1"),
                new WorkTest("TestName2")
        );

        WorkTestLibrary wtl = new WorkTestLibrary("TestLibrary1");
        wtl.setWorkTests(new HashSet<>(workTests));
//        List<Config> mockGet = Arrays.asList(
//                new Config(wtl, (Set<WorkTest>)null),
//                new Config(wtl, (Set<WorkTest>)null)
//                );
        when(_configService.getConfigs())
                .thenReturn(List.of(
                        new Config(wtl, new HashSet<>(workTests)),
                        new Config(wtl, new HashSet<>(workTests))
                ));
        MvcResult result =
                this._mockMvc.perform(get("/config/")).andDo(print()).andExpect(status().isFound()).andReturn();
    }
}
