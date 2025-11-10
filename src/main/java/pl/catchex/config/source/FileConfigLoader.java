package pl.catchex.config.source;

import org.yaml.snakeyaml.Yaml;
import pl.catchex.config.AppConfiguration;
import pl.catchex.bootstrap.PathProvider;
import pl.catchex.bootstrap.DefaultPathProvider;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileConfigLoader implements ConfigSource {

    private static final Logger logger = LoggerFactory.getLogger(FileConfigLoader.class);

    private final PathProvider pathProvider;

    public FileConfigLoader() {
        this.pathProvider = new DefaultPathProvider();
    }

    // constructor for tests
    public FileConfigLoader(PathProvider pathProvider) {
        this.pathProvider = pathProvider;
    }

    @Override
    public Optional<AppConfiguration> loadAppConfiguration() {
        java.nio.file.Path configPath = pathProvider.getConfigPath();
        if (!Files.exists(configPath)) {
            logger.warn("Config file not found at {}", configPath);
            return Optional.empty();
        }

        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(configPath)) {
            return Optional.of(yaml.loadAs(in, AppConfiguration.class));
        } catch (Exception e) {
            logger.warn("Cannot load configuration from {} [ errorMessage={} ]", configPath, e.getMessage());
            return Optional.empty();
        }
    }
}
