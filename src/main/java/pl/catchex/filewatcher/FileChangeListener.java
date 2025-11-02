package pl.catchex.filewatcher;

import java.nio.file.Path;

@FunctionalInterface
public interface FileChangeListener {
    void onFileModified(Path filePath);
}
