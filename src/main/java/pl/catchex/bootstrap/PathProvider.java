package pl.catchex.bootstrap;

import java.nio.file.Path;

public interface PathProvider {
    Path getUserHome();
    Path getAppDir();
    Path getConfigPath();
    Path getTasksPath();
}

