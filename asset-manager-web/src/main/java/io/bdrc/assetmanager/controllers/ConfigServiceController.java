package io.bdrc.assetmanager.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bdrc.assetmanager.config.Config;
import io.bdrc.assetmanager.config.ConfigService;
import io.bdrc.assetmanager.config.SelectedTest;
import net.bytebuddy.asm.Advice;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class ConfigServiceController {

    private final ConfigService configService;

    public ConfigServiceController(final ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping(value="/configs/")
    public ResponseEntity<List<Config>> AllConfigs() {
        return  new ResponseEntity<>(configService.getConfigs(), HttpStatus.FOUND );
    }

    @GetMapping(value="/config/{id}/")
    public ResponseEntity<Config> ConfigById(@PathVariable(name = "id") Long id) {
        Optional<Config> config = configService.getConfigById(id);

        if (config.isPresent()) {
            return new ResponseEntity<>(config.get(),  HttpStatus.FOUND );
        }
        else {
            return  new ResponseEntity<>((Config)null,  HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/configs/{id}/")
    public ResponseEntity<Config> putConfigById(@PathVariable(value = "id")Long id,
            @Valid @RequestBody Config configDetails) throws ResourceNotFoundException {
        Config updatedConfig = configService.updateConfig(configDetails);
        return new ResponseEntity<>(updatedConfig,HttpStatus.OK);
    }

    @PostMapping("/configs/")
    public ResponseEntity<Config> newConfig(@Valid @RequestBody Config configDetails) throws ResourceNotFoundException {
        Config updatedConfig = configService.addConfig(configDetails);
        return new ResponseEntity<>(updatedConfig,HttpStatus.OK);
    }
    @GetMapping("/testconfig/{id}")
    public ResponseEntity<Config> getConfig(@PathVariable Long id) throws JsonProcessingException {
        Optional<Config> look =  configService.getConfigById(id);
        String s ;
        String s2 ;
        if (look.isPresent()) {
            ObjectMapper om = new ObjectMapper();
            Config cfg = look.get();
            Set<SelectedTest> wt = cfg.getSelectedTests();

            s  = om.writeValueAsString(cfg);
            s2 = om.writeValueAsString(wt);
        }
        if (look.isPresent()) {
            return new ResponseEntity<>(look.get(),  HttpStatus.FOUND );
        }
        else {
            return  new ResponseEntity<>((Config)null,  HttpStatus.NOT_FOUND);
        }
    }

}
