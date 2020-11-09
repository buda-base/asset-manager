package io.bdrc.assetmanager.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ConfigServiceController {

    private final ConfigService configService;

    public ConfigServiceController(final ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping(value="/config/")
    public ResponseEntity<List<Config>> getAllConfigs() {
        return  new ResponseEntity<>(configService.getConfigs(), HttpStatus.FOUND );
    }
}
