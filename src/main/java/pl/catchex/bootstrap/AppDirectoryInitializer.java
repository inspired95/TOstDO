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
    public static final String APP_DIR_NAME = ".TOstDO";
    public static final String CONFIG_FILENAME = "config.yaml";
    public static final String RESOURCE_CONFIGURATION = "configuration.yaml";
    public static final String TODO_FILENAME = "todo.md";

    private final FileSystemService fs;
    private final TodoContentProvider todoContentProvider;
    private final ConfigCreator configCreator;

    // prevent no-arg construction from outside - prefer injection
    public AppDirectoryInitializer(FileSystemService fs, TodoContentProvider todoContentProvider) {
        this(fs, todoContentProvider, new DefaultConfigCreatorImpl(fs));
    }

    // Preferred constructor for DI: accept ConfigCreator
    public AppDirectoryInitializer(FileSystemService fs, TodoContentProvider todoContentProvider, ConfigCreator configCreator) {
        this.fs = fs;
        this.todoContentProvider = todoContentProvider;
        this.configCreator = configCreator;
    }

    /**
     * Backwards-compatible convenience: use the real file system and run safely (logs exceptions).
     */
    public static void initializeSafely() {
        FileSystemService fs = new RealFileSystemService();
        AppDirectoryInitializer initializer = new AppDirectoryInitializer(fs, new TodoContentProvider(), new DefaultConfigCreatorImpl(fs));
        try {
            initializer.initialize();
        } catch (IOException e) {
            logger.warn("Failed to initialize application directory or default files: {}", e.getMessage());
        } catch (Exception e) {
            logger.warn("Unexpected error during app directory initialization: {}", e.getMessage());
        }
    }

    /**
     * Backwards-compatible convenience: allow tests to call AppDirectoryInitializer.initialize(fs)
     */
    public static void initialize(FileSystemService fs) throws IOException {
        AppDirectoryInitializer initializer = new AppDirectoryInitializer(fs, new TodoContentProvider());
        initializer.initialize();
    }

    /**
     * Perform initialization. Throws IOException to allow tests to observe failures.
     */
    public void initialize() throws IOException {
        Path home = fs.getUserHome();
        Path appDir = home.resolve(APP_DIR_NAME);

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
        // Use injected ConfigCreator (constructor ensures it's non-null)
        this.configCreator.createDefaultConfig(appDir);
    }

    private void ensureTodo(Path appDir) throws IOException {
        Path todoFile = appDir.resolve(TODO_FILENAME);
        if (!fs.exists(todoFile)) {
            writeSampleTodo(todoFile);
            logger.info("Sample todo file created at {}", todoFile);
        } else {
            logger.debug("Todo file already exists: {}", todoFile);
        }
    }

    private void writeSampleTodo(Path todoFile) throws IOException {
        String sample = todoContentProvider.getSampleContent();
        fs.writeString(todoFile, sample, StandardCharsets.UTF_8);
    }
}
