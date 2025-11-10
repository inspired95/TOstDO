package pl.catchex.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Concrete implementation that is responsible for creating the default configuration file
 * and writing it to the user's application directory. Depends on FileSystemService and is
 * suitable for injection/mocking in tests.
 */
public class DefaultConfigCreatorImpl implements ConfigCreator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConfigCreatorImpl.class);
    private static final String PLACEHOLDER = "[path_to_todo.md_file]";

    private final FileSystemService fs;

    public DefaultConfigCreatorImpl(FileSystemService fs) {
        this.fs = Objects.requireNonNull(fs, "fs must not be null");
    }

    @Override
    public void createDefaultConfig(Path appDir) throws IOException {
        // Use the provided appDir (injected during tests) instead of DefaultPaths, to keep behavior testable
        Path targetConfig = appDir.resolve(AppDirectoryInitializer.CONFIG_FILENAME);
        if (fs.exists(targetConfig)) {
            logger.debug("Configuration file already exists: {}", targetConfig);
            return;
        }

        try (InputStream in = fs.getResourceAsStream(AppDirectoryInitializer.RESOURCE_CONFIGURATION)) {
            if (in == null) {
                logger.warn("Default resource '{}' not found on classpath, skipping creating {}", AppDirectoryInitializer.RESOURCE_CONFIGURATION, targetConfig);
                return;
            }

            // Read full content in a concise, charset-safe manner
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            String newTodoPath = appDir.resolve(AppDirectoryInitializer.TODO_FILENAME).toString();
            String modified = applyTodoPathReplacement(content, newTodoPath);

            logger.debug("DefaultConfigCreator: modified config content:\n{}", modified);

            fs.writeString(targetConfig, modified, StandardCharsets.UTF_8);
            logger.info("Default configuration copied to {} (with updated todoFilePath)", targetConfig);
        }
    }

    private static String applyTodoPathReplacement(String content, String newTodoPath) {
        if (content == null) {
            throw new IllegalStateException("Default configuration content is null");
        }
        if (!content.contains(PLACEHOLDER)) {
            throw new IllegalArgumentException("Could not find placeholder '" + PLACEHOLDER + "' in default configuration content.");
        }

        // Replace all occurrences of the placeholder with a single-quoted, escaped path
        String replacement = "'" + escapeSingleQuotes(newTodoPath) + "'";
        return content.replace(PLACEHOLDER, replacement);
    }

    private static String escapeSingleQuotes(String s) {
        return s.replace("'", "\\'");
    }
}
