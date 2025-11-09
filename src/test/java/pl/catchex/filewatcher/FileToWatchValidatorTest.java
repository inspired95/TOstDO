package pl.catchex.filewatcher;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileToWatchValidatorTest {

    @Test
    void nullPathReturnsPathNull() {
        // when / then
        assertEquals(FileValidationResult.PATH_NULL, FileToWatchValidator.validate(null));
    }

    @Test
    void rootPathReturnsIsRootPath() {
        // given
        Path p = Path.of("/");

        // when / then
        assertEquals(FileValidationResult.IS_ROOT_PATH, FileToWatchValidator.validate(p));
    }

    @Test
    void nonExistingParentDir() {
        // given
        Path tmp = Path.of(System.getProperty("java.io.tmpdir"), "nonexistent-dir-for-test", "nofile.txt");

        // when
        FileValidationResult r = FileToWatchValidator.validate(tmp);

        // then
        assertEquals(FileValidationResult.PARENT_DIR_NOT_FOUND, r);
    }

    @Test
    void parentNotADirectory() throws Exception {
        // given
        Path tmpFile = Files.createTempFile("testfile", ".txt");
        try {
            Path p = tmpFile.resolve("child");

            // when / then
            assertEquals(FileValidationResult.PARENT_NOT_A_DIRECTORY, FileToWatchValidator.validate(p));
        } finally {
            Files.deleteIfExists(tmpFile);
        }
    }

    @Test
    void fileNotFound() {
        // given
        Path tmp = Path.of(System.getProperty("java.io.tmpdir"), "nonexistent-file-for-test.txt");

        // when
        FileValidationResult r = FileToWatchValidator.validate(tmp);

        // then
        assertEquals(FileValidationResult.FILE_NOT_FOUND, r);
    }

    @Test
    void notARegularFile() throws Exception {
        // given
        Path dir = Files.createTempDirectory("testdir");
        try {
            Path p = dir.resolve("subdir");
            Files.createDirectory(p);

            // when
            FileValidationResult r = FileToWatchValidator.validate(p);

            // then
            assertEquals(FileValidationResult.NOT_A_REGULAR_FILE, r);
        } finally {
            // Ensure the stream returned by Files.walk(...) is closed by using try-with-resources
            try (Stream<Path> stream = Files.walk(dir)) {
                stream.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (Exception ignore) {
                                // NOOP
                            }
                        });
            } catch (Exception ignore) {
                // NOOP
            }
        }
    }
}
