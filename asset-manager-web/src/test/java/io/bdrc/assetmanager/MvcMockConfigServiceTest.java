package io.bdrc.assetmanager;

import io.bdrc.assetmanager.WorkTest.WorkTest;
import io.bdrc.assetmanager.WorkTestLibrary.WorkTestLibrary;
import io.bdrc.assetmanager.config.Config;
import io.bdrc.assetmanager.config.ConfigService;
import io.bdrc.assetmanager.config.ConfigServiceController;
import org.junit.jupiter.api.BeforeEach;
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

    private List<WorkTest> _workTests ;
    private WorkTestLibrary _workTestLibrary ;

    @BeforeEach
    public void setup() {
        _workTests = Arrays.asList(
                new WorkTest("TestName1"),
                new WorkTest("TestName2")
        );

        _workTestLibrary = new WorkTestLibrary("TestLibrary1");
        _workTestLibrary.setWorkTests(new HashSet<>(_workTests));
//        List<Config> mockGet = Arrays.asList(
//                new Config(wtl, (Set<WorkTest>)null),
//                new Config(wtl, (Set<WorkTest>)null)
//                );

    }
    @Test
    public void webReturnAllFromService() throws Exception {

        when(_configService.getConfigs())
                .thenReturn(List.of(
                        new Config(_workTestLibrary, new HashSet<>(_workTests)),
                        new Config(_workTestLibrary, new HashSet<>(_workTests))
                ));
                this._mockMvc.perform(get("/configs/")).andDo(print()).andExpect(status().isFound())
                        .andExpect(jsonPath("$[0].workTestLibrary.path").value(_workTestLibrary.getPath()))
                        .andExpect(jsonPath("$[0].workTestLibrary.workTests[0].testName").value(_workTests.get(0).getTestName()));
    }

    @Test
    public void webReturnByIdFromService() throws Exception {
        when(_configService.getConfigById(5L))
                .thenReturn(Optional.of(new Config(_workTestLibrary, new HashSet<>(_workTests))));

        this._mockMvc.perform(get("/config/5/")).andDo(print()).andExpect(status().isFound())
                .andExpect(jsonPath("$.workTestLibrary.path").value(_workTestLibrary.getPath()))
                .andExpect(jsonPath("$.workTestLibrary.workTests[0].testName").value(_workTests.get(0).getTestName()));

        this._mockMvc.perform(get("/config/999/")).andDo(print()).andExpect(status().isNotFound());

    }    @Test
    public void webReturnNullNotfoundByIdFromService() throws Exception {
        when(_configService.getConfigById(5L))
                .thenReturn(Optional.of(new Config(_workTestLibrary, new HashSet<>(_workTests))));

        this._mockMvc.perform(get("/config/999/")).andDo(print()).andExpect(status().isNotFound());

    }
}

/*
GET http://localhost:8080/config/

HTTP/1.1 302
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 10 Nov 2020 16:41:12 GMT
Keep-Alive: timeout=60
Connection: keep-alive

[
  {
    "id": 1,
    "workTestLibrary": {
      "id": 2,
      "workTests": [
        {
          "id": 3,
          "testName": "WorkTestName1",
          "workTestParameters": [
            {
              "id": 4,
              "name": "series 1 t=1 p=1",
              "value": "value t=1 p=1"
            }
          ]
        },
        {
          "id": 5,
          "testName": "WorkTestName2",
          "workTestParameters": [
            {
              "id": 7,
              "name": "series 1 t=2 p=2",
              "value": "value t=2 p=2"
            },
            {
              "id": 6,
              "name": "series 1 t=2 p=1",
              "value": "value t=2 p=1"
            }
          ]
        },
        {
          "id": 8,
          "testName": "WorkTestName3",
          "workTestParameters": [
            {
              "id": 9,
              "name": "series 1 t=3 p=1",
              "value": "value t=3 p=1"
            },
            {
              "id": 10,
              "name": "series 1 t=3 p=2",
              "value": "value t=3 p=2"
            },
            {
              "id": 11,
              "name": "series 1 t=3 p=3",
              "value": "value t=3 p=3"
            }
          ]
        }
      ],
      "path": "/Apps/testJar1.jar"
    },
    "workTests": []
  },
  {
    "id": 12,
    "workTestLibrary": {
      "id": 13,
      "workTests": [
        {
          "id": 14,
          "testName": "WorkTestName1",
          "workTestParameters": [
            {
              "id": 15,
              "name": "series 2 t=1 p=1",
              "value": "value t=1 p=1"
            }
          ]
        },
        {
          "id": 16,
          "testName": "WorkTestName2",
          "workTestParameters": [
            {
              "id": 18,
              "name": "series 2 t=2 p=2",
              "value": "value t=2 p=2"
            },
            {
              "id": 17,
              "name": "series 2 t=2 p=1",
              "value": "value t=2 p=1"
            }
          ]
        },
        {
          "id": 19,
          "testName": "WorkTestName3",
          "workTestParameters": [
            {
              "id": 21,
              "name": "series 2 t=3 p=2",
              "value": "value t=3 p=2"
            },
            {
              "id": 22,
              "name": "series 2 t=3 p=3",
              "value": "value t=3 p=3"
            },
            {
              "id": 20,
              "name": "series 2 t=3 p=1",
              "value": "value t=3 p=1"
            }
          ]
        }
      ],
      "path": "/Apps/testJar2.jar"
    },
    "workTests": []
  },
  {
    "id": 23,
    "workTestLibrary": {
      "id": 24,
      "workTests": [
        {
          "id": 25,
          "testName": "WorkTestName1",
          "workTestParameters": [
            {
              "id": 26,
              "name": "series 3 t=1 p=1",
              "value": "value t=1 p=1"
            }
          ]
        },
        {
          "id": 27,
          "testName": "WorkTestName2",
          "workTestParameters": [
            {
              "id": 28,
              "name": "series 3 t=2 p=1",
              "value": "value t=2 p=1"
            },
            {
              "id": 29,
              "name": "series 3 t=2 p=2",
              "value": "value t=2 p=2"
            }
          ]
        },
        {
          "id": 30,
          "testName": "WorkTestName3",
          "workTestParameters": [
            {
              "id": 33,
              "name": "series 3 t=3 p=3",
              "value": "value t=3 p=3"
            },
            {
              "id": 32,
              "name": "series 3 t=3 p=2",
              "value": "value t=3 p=2"
            },
            {
              "id": 31,
              "name": "series 3 t=3 p=1",
              "value": "value t=3 p=1"
            }
          ]
        }
      ],
      "path": "/Apps/testJar3.jar"
    },
    "workTests": []
  }
]
 */
