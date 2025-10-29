package pl.catchex.config.source;

import org.yaml.snakeyaml.Yaml;
import pl.catchex.Main;
import pl.catchex.config.AppConfiguration;

import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Logger;

public class ClasspathConfigLoader implements ConfigSource{

    private static final Logger logger = Logger.getLogger(ClasspathConfigLoader.class.getName());

    private static final String APP_CONFIGURATION_FILE_NAME = "configuration.yaml";

    public Optional<AppConfiguration> loadAppConfiguration(){
        return loadConfiguration(APP_CONFIGURATION_FILE_NAME, AppConfiguration.class);
    }

    private <T> Optional<T> loadConfiguration(String configFileName, Class<T> configurationClass){
        Yaml yaml = new Yaml();
        String configFileRelativePath = "/" + configFileName;

        try (InputStream inputStream = Main.class.getResourceAsStream(configFileRelativePath)) {

            if (inputStream == null) {
                logger.warning("File not found " + configFileRelativePath);
                return Optional.empty();
            }
            var a = ClasspathConfigLoader.class;

            return Optional.of(yaml.loadAs(inputStream, configurationClass));
        } catch (Exception e) {
            logger.warning("Cannot load configuration [ " + e.getMessage() + " ]");
        }
        return Optional.empty();
    }
}