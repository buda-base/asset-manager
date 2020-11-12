package io.bdrc.assetmanager.config;

import net.bytebuddy.asm.Advice;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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

}
