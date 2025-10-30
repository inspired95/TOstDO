package pl.catchex.config.cache;

import pl.catchex.config.AppConfiguration;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryConfigCache implements ConfigCache {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryConfigCache.class);

    private AppConfiguration cachedAppConfiguration;

    public void setAppConfiguration(AppConfiguration appConfiguration){
        logger.info("New app configuration saving");
        cachedAppConfiguration = appConfiguration;
    }

    public Optional<AppConfiguration> getAppConfiguration(){
        return Optional.ofNullable(cachedAppConfiguration);
    }
}
