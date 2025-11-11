package pl.catchex.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Ensures application directory and default files exist in user's home.
 * Now instance-based to allow DI of FileSystemService and easier testing.
 */
public class AppDirectoryInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AppDirectoryInitializer.class);

    private final FileSystemService fs;
    private final ConfigCreator configCreator;

    public AppDirectoryInitializer(FileSystemService fs) {
        this(fs, new DefaultConfigCreatorImpl(fs));
    }

    public AppDirectoryInitializer(FileSystemService fs, ConfigCreator configCreator) {
        this.fs = fs;
        this.configCreator = configCreator;
    }

    /**
     * Perform initialization of application directory and default files.
     */
    public void perform() throws IOException {
        Path home = fs.getUserHome();
        Path appDir = home.resolve(AppConstants.APP_DIR_NAME);

        ensureAppDir(appDir);
        ensureConfig(appDir);
        ensureTodo(appDir);
        ensureReadmes(appDir);
    }

    private void ensureAppDir(Path appDir) throws IOException {
        if (!fs.exists(appDir)) {
            fs.createDirectories(appDir);
            logger.info("Created application directory: {}", appDir);
        } else {
            logger.debug("Application directory already exists: {}", appDir);
        }
    }

    private void ensureConfig(Path appDir) throws IOException {
        this.configCreator.createDefaultConfig(appDir);
    }

    private void ensureTodo(Path appDir) throws IOException {
        Path todoFile = appDir.resolve(AppConstants.TODO_FILENAME);
        if (!fs.exists(todoFile)) {
            writeSampleTodo(todoFile);
            logger.info("Sample todo file created at {}", todoFile);
        } else {
            logger.debug("Todo file already exists: {}", todoFile);
        }
    }

    private void writeSampleTodo(Path todoFile) throws IOException {
        try (InputStream in = fs.getResourceAsStream(AppConstants.TODO_FILENAME)) {
            if (in == null) {
                logger.warn("Sample todo resource '{}' not found on classpath, skipping creating {}", AppConstants.TODO_FILENAME, todoFile);
            } else {
                fs.copy(in, todoFile);
                logger.info("Copied resource '{}' to {}", AppConstants.TODO_FILENAME, todoFile);
            }
        }
    }

    private void ensureReadmes(Path appDir) throws IOException {
        String[] readmeResources = {"readme_PL.md", "readme_EN.md"};
        for (String resourceName : readmeResources) {
            Path target = appDir.resolve(resourceName);
            if (fs.exists(target)) {
                logger.debug("Readme already exists: {}", target);
                continue;
            }
            try (InputStream in = fs.getResourceAsStream(resourceName)) {
                if (in == null) {
                    logger.warn("Resource '{}' not found in application resources; skipping creation of {}", resourceName, target);
                } else {
                    fs.copy(in, target);
                    logger.info("Copied resource '{}' to {}", resourceName, target);
                }
            }
        }
    }
}
