package io.bdrc.assetmanager.config;

import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigService implements IConfigService {

    private final ConfigRepository _configRepository;

    public ConfigService(final ConfigRepository configRepository) {
        _configRepository = configRepository;
    }

    @Override
    public List<Config> getConfigs() {
        return (List<Config>) _configRepository.findAll();
    }

    @Override
    public Optional<Config> getConfigById(final long id) {
        return _configRepository.findById(id);
    }

    @Override
    public Config updateConfig(final Config config) throws EntityNotFoundException {
        Config modConfig ;
        if (config.getId() == null || config.getId() == 0) throw new EntityNotFoundException("Config");
        Optional<Config> origConfig = _configRepository.findById(config.getId());
        if (origConfig.isPresent()) {
            modConfig = origConfig.get();
            modConfig.setSelectedTests(config.getSelectedTests());
            modConfig.setworkTestLibrary(config.getworkTestLibrary());
        }
        else throw new EntityNotFoundException(String.format("Config with id %d not found."
                ,config.getId()));
        return _configRepository.save(modConfig);
    }

    @Override
    public Config addConfig(final Config newConfig) {
        return _configRepository.save(newConfig);
    }
}
