package pl.catchex;

import pl.catchex.config.*;
import pl.catchex.config.ConfigurationService;
import pl.catchex.config.cache.InMemoryConfigCache;
import pl.catchex.config.cache.ConfigCache;
import pl.catchex.config.source.ClasspathConfigLoader;
import pl.catchex.config.source.ConfigSource;
import pl.catchex.filewatcher.FileWatcher;
import pl.catchex.frequency.ToDoFrequencyService;
import pl.catchex.model.ToDoIntervalMinutes;
import pl.catchex.model.ToDoItem;
import pl.catchex.reader.ToDoReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
            Path path = Paths.get(config.getConfiguration().getToDoFilePath());
            try {
                ToDoFrequencyService toDoFrequencyService = new ToDoFrequencyService(Clock.systemDefaultZone(), config.getConfiguration().getReminderConfiguration());


                List<ToDoItem> toDoItems = new ToDoReader(config).read();
                toDoItems.forEach(item -> {
                    ToDoIntervalMinutes toDoIntervalMinutes = toDoFrequencyService.calculateToDoInterval(item);
                    logger.info( "ToDo item {} remind in {}", item, toDoIntervalMinutes);

                });
            }catch (IOException ex){
                logger.warn("Cannot read TODOs");
            }
            FileWatcher watcher = null;
            try {
                watcher = new FileWatcher(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {

                Thread start = watcher.start();
                start.join();
            }  catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();

            try {
                watcher.stop();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }, () -> logger.warn("Configuration not loaded. Cannot start program"));
    }
}
