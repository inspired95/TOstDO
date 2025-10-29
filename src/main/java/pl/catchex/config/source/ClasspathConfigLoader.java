package pl.catchex.config.source;

import org.yaml.snakeyaml.Yaml;
import pl.catchex.Main;
import pl.catchex.config.AppConfiguration;

import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClasspathConfigLoader implements ConfigSource{

    private static final Logger logger = Logger.getLogger(ClasspathConfigLoader.class.getName());

    private static final String APP_CONFIGURATION_FILE_NAME = "configuration.yaml";

    public Optional<AppConfiguration> loadAppConfiguration(){
        return loadConfiguration(APP_CONFIGURATION_FILE_NAME, AppConfiguration.class);
    }

    private <T> Optional<T> loadConfiguration(String configFileName, Class<T> configurationClass){
        Yaml yaml = new Yaml();

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(configFileName)) {

            if (inputStream == null) {
                logger.log(Level.WARNING,"File not found [ fileName={0} ]", configFileName);
                return Optional.empty();
            }
            return Optional.of(yaml.loadAs(inputStream, configurationClass));
        } catch (Exception e) {
            logger.log(Level.WARNING,"Cannot load configuration [ errorMessage={0} ]", e.getMessage());
        }
        return Optional.empty();
    }
}