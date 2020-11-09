package io.bdrc.assetmanager.config;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigService implements IConfigService{

    private final ConfigRepository _configRepository ;

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
    public void putConfig(final Config config) {
        _configRepository.save(config);
    }
}
