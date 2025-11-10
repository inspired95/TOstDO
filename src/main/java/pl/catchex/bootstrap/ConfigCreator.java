package pl.catchex.bootstrap;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface describing a service that creates default configuration files for the application.
 * Extracted to allow different implementations and easier testing (DI).
 */
public interface ConfigCreator {
    void createDefaultConfig(Path appDir) throws IOException;
}

