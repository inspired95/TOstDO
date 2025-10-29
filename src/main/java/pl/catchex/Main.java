package pl.catchex;

import pl.catchex.config.*;
import pl.catchex.config.cache.ConfigurationService;
import pl.catchex.config.cache.InMemoryConfigCache;
import pl.catchex.config.reader.ConfigCache;
import pl.catchex.config.source.ClasspathConfigLoader;
import pl.catchex.config.source.ConfigSource;
import pl.catchex.model.ToDoItem;
import pl.catchex.reader.ToDoReader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());


    public static void main(String[] args) throws IOException {
        ConfigCache configCache = new InMemoryConfigCache();
        ConfigSource configSource = new ClasspathConfigLoader();

        ConfigurationService configurationService = new ConfigurationService(configCache, configSource);
        Optional<AppConfiguration> appConfiguration = configurationService.getAppConfiguration();
        appConfiguration.ifPresentOrElse( config -> {
            try {
                List<ToDoItem> toDoItems = new ToDoReader(config).read(Paths.get(config.getConfiguration().getToDoFilePath()));
                toDoItems.forEach(System.out::println);
            }catch (IOException ex){
                logger.warning("Cannot read TODOs");
            }

        }, () -> logger.warning("Configuration not loaded. Cannot start program"));


    }
}
