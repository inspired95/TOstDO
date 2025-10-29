package pl.catchex.config;

import pl.catchex.config.cache.ConfigCache;
import pl.catchex.config.source.ConfigSource;

import java.util.Optional;

public class ConfigurationService {
    
    private final ConfigCache configCache;

    private final ConfigSource configSource;

    public ConfigurationService(ConfigCache configCache, ConfigSource configSource){
        this.configCache = configCache;
        this.configSource = configSource;
    }

    public Optional<AppConfiguration> getAppConfiguration(){
        return configCache.getAppConfiguration()//
                .or(configSource::loadAppConfiguration);
    }
}
