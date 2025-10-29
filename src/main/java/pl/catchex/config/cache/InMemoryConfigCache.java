package pl.catchex.config.cache;

import pl.catchex.config.AppConfiguration;

import java.util.Optional;
import java.util.logging.Logger;

public class InMemoryConfigCache implements ConfigCache {

    private static final Logger logger = Logger.getLogger(InMemoryConfigCache.class.getName());

    private AppConfiguration cachedAppConfiguration;

    public void setAppConfiguration(AppConfiguration appConfiguration){
        logger.info("New app configuration saving");
        cachedAppConfiguration = appConfiguration;
    }

    public Optional<AppConfiguration> getAppConfiguration(){
        return Optional.ofNullable(cachedAppConfiguration);
    }
}
