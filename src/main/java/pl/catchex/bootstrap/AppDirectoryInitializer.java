package pl.catchex.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Ensures application directory and default files exist in user's home.
 * Now instance-based to allow DI of FileSystemService and easier testing.
 */
public class AppDirectoryInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AppDirectoryInitializer.class);

    private final FileSystemService fs;
    private final SampleTodoContentProvider sampleTodoContentProvider;
    private final ConfigCreator configCreator;

    public AppDirectoryInitializer(FileSystemService fs, SampleTodoContentProvider sampleTodoContentProvider) {
        this(fs, sampleTodoContentProvider, new DefaultConfigCreatorImpl(fs));
    }

    public AppDirectoryInitializer(FileSystemService fs, SampleTodoContentProvider sampleTodoContentProvider, ConfigCreator configCreator) {
        this.fs = fs;
        this.sampleTodoContentProvider = sampleTodoContentProvider;
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
        String sample = sampleTodoContentProvider.getSampleContent();
        fs.writeString(todoFile, sample, StandardCharsets.UTF_8);
    }
}
