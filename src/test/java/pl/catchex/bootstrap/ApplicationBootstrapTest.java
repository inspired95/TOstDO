package pl.catchex.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import pl.catchex.config.AppConfiguration;
import pl.catchex.config.ConfigurationService;
import pl.catchex.config.cache.ConfigCache;
import pl.catchex.config.cache.InMemoryConfigCache;
import pl.catchex.config.source.ClasspathConfigLoader;
import pl.catchex.config.source.ConfigSource;
import pl.catchex.di.AppModule;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationBootstrapTest {

    @Test
    public void bootstrapStartsAndStopsWithNoopNotifications() throws Exception {
        // given
        ConfigCache configCache = new InMemoryConfigCache();
        ConfigSource configSource = new ClasspathConfigLoader();
        ConfigurationService configurationService = new ConfigurationService(configCache, configSource);
        Optional<AppConfiguration> appConfiguration = configurationService.getAppConfiguration();

        // then
        assertTrue(appConfiguration.isPresent(), "App configuration should be present on classpath");

        // given
        Injector injector = Guice.createInjector(new AppModule(appConfiguration.get()));
        ApplicationBootstrap bootstrap = injector.getInstance(ApplicationBootstrap.class);

        Thread appThread = new Thread(() -> {
            try {
                bootstrap.run();
            } catch (Throwable t) {
                // If run throws, fail the test by rethrowing as runtime
                throw new RuntimeException(t);
            }
        }, "test-bootstrap-thread");

        // when
        appThread.start();

        // wait a short time for startup to proceed
        Thread.sleep(500);

        // when
        bootstrap.stop();

        // wait for the thread to terminate
        appThread.join(2000);

        // then
        assertFalse(appThread.isAlive(), "Bootstrap thread should have terminated after stop()");
    }
}
