package pl.catchex.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import pl.catchex.config.AppConfiguration;
import pl.catchex.config.ConfigurationService;
import pl.catchex.config.cache.ConfigCache;
import pl.catchex.config.cache.InMemoryConfigCache;
import pl.catchex.config.source.ConfigSource;
import pl.catchex.config.source.FileConfigLoader;
import pl.catchex.di.AppModule;
import pl.catchex.di.BootstrapModule;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationBootstrapTest {

    @Test
    void bootstrapStartsAndStopsWithNoopNotifications() throws Exception {
        // Ensure default application directory and config exist (matches runtime behavior)
        Injector bootstrapInjector = Guice.createInjector(new BootstrapModule());
        AppDirectoryInitializer initializer = bootstrapInjector.getInstance(AppDirectoryInitializer.class);
        initializer.perform();

        // given
        ConfigCache configCache = new InMemoryConfigCache();
        ConfigSource configSource = new FileConfigLoader();
        ConfigurationService configurationService = new ConfigurationService(configCache, configSource);
        Optional<AppConfiguration> appConfiguration = configurationService.getAppConfiguration();

        // then
        assertTrue(appConfiguration.isPresent(), "App configuration should be present on default config path");

        // given
        Injector injector = Guice.createInjector(new AppModule(appConfiguration.get()));
        final ApplicationAssembler assembler = injector.getInstance(ApplicationAssembler.class);

        // Use a latch to wait until the bootstrap run() actually starts
        CountDownLatch started = new CountDownLatch(1);

        // Create a test ApplicationBootstrap that signals when run() begins
        class TestApplicationBootstrap extends pl.catchex.bootstrap.ApplicationBootstrap {
            TestApplicationBootstrap(ApplicationAssembler assembler, AppDirectoryInitializer initializer) {
                super(assembler, initializer);
            }

            @Override
            public void run() {
                // signal that run() is starting
                started.countDown();
                super.run();
            }
        }

        // instantiate test bootstrap using the same initializer we used earlier
        TestApplicationBootstrap testBootstrap = new TestApplicationBootstrap(assembler, initializer);

        Thread appThread = new Thread(() -> {
            try {
                testBootstrap.run();
            } catch (Throwable t) {
                // If run throws, fail the test by rethrowing as runtime
                throw new RuntimeException(t);
            }
        }, "test-bootstrap-thread");

        // when
        appThread.start();

        // wait for bootstrap to actually start (replace Thread.sleep)
        boolean startedOk = started.await(2, TimeUnit.SECONDS);
        assertTrue(startedOk, "Bootstrap did not start within timeout");

        // when
        testBootstrap.stop();

        // wait for the thread to terminate
        appThread.join(2000);

        // then
        assertFalse(appThread.isAlive(), "Bootstrap thread should have terminated after stop()");
    }
}
