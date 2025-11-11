package pl.catchex.bootstrap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AppDirectoryInitializerResourceMissingTest {

    static class DummyFS implements FileSystemService {
        private final Path home;
        private final Set<Path> created = new HashSet<>();

        DummyFS(Path home) {
            this.home = home;
        }

        @Override
        public Path getUserHome() {
            return home;
        }

        @Override
        public boolean exists(Path path) {
            return created.contains(path);
        }

        @Override
        public void createDirectories(Path path) throws IOException {
            Files.createDirectories(path);
            created.add(path);
        }

        @Override
        public InputStream getResourceAsStream(String resourceName) {
            // Simulate missing resource
            return null;
        }

        @Override
        public void copy(InputStream in, Path target) {
            throw new UnsupportedOperationException("copy should not be called when resource is missing");
        }

        @Override
        public void writeString(Path path, String content, java.nio.charset.Charset charset) throws IOException {
            Files.writeString(path, content, charset);
            created.add(path);
        }
    }

    @Test
    void perform_handlesMissingResourceGracefully(@TempDir Path tempDir) throws Exception {
        DummyFS fs = new DummyFS(tempDir);

        // call initializer with our dummy fs (resource stream will be null)
        new AppDirectoryInitializer(fs).perform();

        Path appDir = tempDir.resolve(AppConstants.APP_DIR_NAME);
        assertTrue(Files.exists(appDir), "app dir should still be created");

        Path config = appDir.resolve(AppConstants.CONFIG_FILENAME);
        assertFalse(Files.exists(config), "config.yaml should NOT be created when resource is missing");

        Path todo = appDir.resolve(AppConstants.TODO_FILENAME);
        assertFalse(Files.exists(todo), "todo.md should NOT be created when resource is missing");
    }
}
