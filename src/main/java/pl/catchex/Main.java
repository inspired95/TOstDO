package pl.catchex;

import pl.catchex.config.*;
import pl.catchex.config.ConfigurationService;
import pl.catchex.config.cache.InMemoryConfigCache;
import pl.catchex.config.cache.ConfigCache;
import pl.catchex.config.source.ClasspathConfigLoader;
import pl.catchex.config.source.ConfigSource;
import pl.catchex.model.ToDoItem;
import pl.catchex.reader.ToDoReader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        ConfigCache configCache = new InMemoryConfigCache();
        ConfigSource configSource = new ClasspathConfigLoader();

        ConfigurationService configurationService = new ConfigurationService(configCache, configSource);
        Optional<AppConfiguration> appConfiguration = configurationService.getAppConfiguration();
        appConfiguration.ifPresentOrElse( config -> {
            try {
                List<ToDoItem> toDoItems = new ToDoReader(config).read(Paths.get(config.getConfiguration().getToDoFilePath()));
                toDoItems.forEach(item -> logger.info(String.valueOf(item)));
            }catch (IOException ex){
                logger.warn("Cannot read TODOs");
            }

        }, () -> logger.warn("Configuration not loaded. Cannot start program"));


    }
}
