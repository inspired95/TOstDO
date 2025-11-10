package pl.catchex;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.bootstrap.AppDirectoryInitializer;
import pl.catchex.bootstrap.ApplicationBootstrap;
import pl.catchex.config.AppConfiguration;
import pl.catchex.config.ConfigurationService;
import pl.catchex.config.cache.ConfigCache;
import pl.catchex.config.cache.InMemoryConfigCache;
import pl.catchex.config.source.ConfigSource;
import pl.catchex.config.source.FileConfigLoader;
import pl.catchex.di.AppModule;
import pl.catchex.di.BootstrapModule;
import pl.catchex.tray.NotificationSenderFactory;

import java.util.Optional;

public class TOstDOApplication {

    private static final Logger logger = LoggerFactory.getLogger(TOstDOApplication.class);

    public static void main(String[] args) {
        logger.info("TOstDO application starting...");

        Injector bootstrapInjector = Guice.createInjector(new BootstrapModule());
        try {
            bootstrapInjector.getInstance(AppDirectoryInitializer.class).perform();
        } catch (Exception e) {
            logger.warn("Failed to run bootstrap initializer: {}", e.getMessage());
        }

        ConfigCache configCache = new InMemoryConfigCache();
        ConfigSource configSource = new FileConfigLoader();

        ConfigurationService configurationService = new ConfigurationService(configCache, configSource);
        Optional<AppConfiguration> appConfiguration = configurationService.getAppConfiguration();

        appConfiguration.ifPresentOrElse(config -> {
            logger.info("Configuration  loaded");
            Injector injector = Guice.createInjector(new AppModule(config, NotificationSenderFactory.createDefaultTrayService()));
            injector.getInstance(ApplicationBootstrap.class).run();
        }, () -> logger.error("Configuration not loaded"));
    }
}
