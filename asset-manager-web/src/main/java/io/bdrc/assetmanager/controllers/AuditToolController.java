package io.bdrc.assetmanager.controllers;
/*
This code uses Spring @RestController annotation, which marks the class as a controller where every method returns a domain object instead of a view. It is shorthand for including both
@Controller and @ResponseBody

Implicitly, this controller serves rest data when the application.properties property spring.data.rest.base-path=/api
is appended.  See io.bdrc.assetmanager.repositories
 */

/*
This code uses Spring @RestController annotation, which marks the class as a controller where every method returns a domain object instead of a view. It is shorthand for including both
@Controller and @ResponseBody
 */


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bdrc.assetmanager.WorkTest.RunnableTest;
import io.bdrc.assetmanager.config.Config;
import io.bdrc.assetmanager.config.ConfigRepository;
import io.bdrc.assetmanager.config.SelectedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

@RestController
public class AuditToolController {
    @Autowired
    private ConfigRepository _configRepository ;

    // @RequestParam(value="id",defaultValue = "1") always sets param as 1

}
