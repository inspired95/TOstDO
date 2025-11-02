package pl.catchex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.config.AppConfiguration;
import pl.catchex.config.ConfigurationService;
import pl.catchex.config.cache.ConfigCache;
import pl.catchex.config.cache.InMemoryConfigCache;
import pl.catchex.config.source.ClasspathConfigLoader;
import pl.catchex.config.source.ConfigSource;
import pl.catchex.filewatcher.FileWatcher;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TOstDOApplication {

    private static final Logger logger = LoggerFactory.getLogger(TOstDOApplication.class);

    private static final ExecutorService executor = Executors.newFixedThreadPool(1);
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private static FileWatcher todoFileWatcher;

    public static void main(String[] args) {
        logger.info("TOstDO application starting...");

        ConfigCache configCache = new InMemoryConfigCache();
        ConfigSource configSource = new ClasspathConfigLoader();

        ConfigurationService configurationService = new ConfigurationService(configCache, configSource);
        Optional<AppConfiguration> appConfiguration = configurationService.getAppConfiguration();

        appConfiguration.ifPresentOrElse( config -> {
            try {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    logger.info("Shutdown signal received -> TOstDO application closing procedure initiation");
                    executor.shutdownNow();
                    if(todoFileWatcher != null){
                        try {
                            todoFileWatcher.stop();
                        } catch (IOException e) {
                            logger.error("I/O exception [ message={} ]", e.getMessage());
                        }
                    }
                }));

                todoFileWatcher = new FileWatcher(Paths.get(config.getConfiguration().getToDoFilePath()));
                todoFileWatcher.start();

                shutdownLatch.await();
            } catch (InterruptedException e) {
                logger.error("Main thread interrupted [ message={} ]", e.getMessage());
            } catch (IOException e) {
                logger.error("I/O exception [ message={} ]", e.getMessage());
            } finally {
                logger.info("All task finished -> shutdown main thread");
                if (!executor.isTerminated()) {
                    executor.shutdownNow();
                }
                logger.warn("TOstDO application completely finished");
            }
        }, () -> logger.error("Configuration not loaded"));
    }
}
