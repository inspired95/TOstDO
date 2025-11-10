package pl.catchex.bootstrap;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Default implementation of PathProvider that delegates to a FileSystemService for the user home
 * and constructs app-specific paths based on AppDirectoryInitializer constants.
 */
public class DefaultPathProvider implements PathProvider {

    private final FileSystemService fs;

    public DefaultPathProvider(FileSystemService fs) {
        this.fs = Objects.requireNonNull(fs, "fs must not be null");
    }

    public DefaultPathProvider() {
        this(new RealFileSystemService());
    }

    @Override
    public Path getUserHome() {
        return fs.getUserHome();
    }

    @Override
    public Path getAppDir() {
        return getUserHome().resolve(AppDirectoryInitializer.APP_DIR_NAME);
    }

    @Override
    public Path getConfigPath() {
        return getAppDir().resolve(AppDirectoryInitializer.CONFIG_FILENAME);
    }

    @Override
    public Path getTodoPath() {
        return getAppDir().resolve(AppDirectoryInitializer.TODO_FILENAME);
    }
}

