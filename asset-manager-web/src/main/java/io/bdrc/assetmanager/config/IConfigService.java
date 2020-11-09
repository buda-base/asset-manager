package io.bdrc.assetmanager.config;

import java.util.List;
import java.util.Optional;

public interface IConfigService {

    List<Config> getConfigs() ;

    Optional<Config> getConfigById(long id) ;

    void putConfig(Config config) ;


}
