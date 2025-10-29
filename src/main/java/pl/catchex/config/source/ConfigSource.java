package pl.catchex.config.source;

import pl.catchex.config.AppConfiguration;

import java.util.Optional;

public interface ConfigSource {
    Optional<AppConfiguration> loadAppConfiguration();
}
