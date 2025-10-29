package pl.catchex.config.cache;

import pl.catchex.config.AppConfiguration;
import pl.catchex.config.reader.ConfigCache;
import pl.catchex.config.source.ConfigSource;

import java.util.Optional;

public class ConfigurationService {
    
    private ConfigCache configCache;

    private ConfigSource configSource;

    public ConfigurationService(ConfigCache configCache, ConfigSource configSource){
        this.configCache = configCache;
        this.configSource = configSource;
    }

    public Optional<AppConfiguration> getAppConfiguration(){
        return configCache.getAppConfiguration()//
                .or( () ->configSource.loadAppConfiguration());
    }
}
