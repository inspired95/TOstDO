package pl.catchex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.config.AppConfiguration;
import pl.catchex.config.ConfigurationService;
import pl.catchex.config.cache.ConfigCache;
import pl.catchex.config.cache.InMemoryConfigCache;
import pl.catchex.config.source.ClasspathConfigLoader;
import pl.catchex.config.source.ConfigSource;

import java.util.Optional;

public class TOstDOApplication {

    private static final Logger logger = LoggerFactory.getLogger(TOstDOApplication.class);

    public static void main(String[] args) {
        logger.info("TOstDO application starting...");

        ConfigCache configCache = new InMemoryConfigCache();
        ConfigSource configSource = new ClasspathConfigLoader();

        ConfigurationService configurationService = new ConfigurationService(configCache, configSource);
        Optional<AppConfiguration> appConfiguration = configurationService.getAppConfiguration();

        appConfiguration.ifPresentOrElse(config -> {
            ApplicationAssembler assembler = new ApplicationAssembler(config);
            assembler.run();
        }, () -> logger.error("Configuration not loaded"));
    }
}
