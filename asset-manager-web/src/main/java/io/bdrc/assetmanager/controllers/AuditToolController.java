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


import io.bdrc.assetmanager.config.Config;
import io.bdrc.assetmanager.config.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuditToolController {
    @Autowired
    private ConfigRepository _configRepository ;

    @GetMapping("/config/{id}")
    public Optional<Config> getConfig(@RequestParam(value="userId",defaultValue = "1") Long userId) {
        return _configRepository.findById(userId);
    }
}
