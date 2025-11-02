package pl.catchex.config.source;

import org.yaml.snakeyaml.Yaml;
import pl.catchex.config.AppConfiguration;

import java.io.InputStream;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathConfigLoader implements ConfigSource{

    private static final Logger logger = LoggerFactory.getLogger(ClasspathConfigLoader.class);

    private static final String APP_CONFIGURATION_FILE_NAME = "configuration.yaml";

    public Optional<AppConfiguration> loadAppConfiguration(){
        return loadConfiguration(APP_CONFIGURATION_FILE_NAME, AppConfiguration.class);
    }

    private <T> Optional<T> loadConfiguration(String configFileName, Class<T> configurationClass){
        Yaml yaml = new Yaml();

        try (InputStream inputStream = ClasspathConfigLoader.class.getClassLoader().getResourceAsStream(configFileName)) {

            if (inputStream == null) {
                logger.warn("File not found [ fileName={} ]", configFileName);
                return Optional.empty();
            }
            return Optional.of(yaml.loadAs(inputStream, configurationClass));
        } catch (Exception e) {
            logger.warn("Cannot load configuration [ errorMessage={} ]", e.getMessage());
        }
        return Optional.empty();
    }
}