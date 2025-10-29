package pl.catchex.config.cache;

import pl.catchex.config.AppConfiguration;

import java.util.Optional;

public interface ConfigCache {
    Optional<AppConfiguration> getAppConfiguration();

    void setAppConfiguration(AppConfiguration appConfiguration);
}
