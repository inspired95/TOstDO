package pl.catchex.filewatcher;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class FileToWatchValidatorTest {

    @Test
    void nullPathReturnsPathNull() {
        assertEquals(FileValidationResult.PATH_NULL, FileToWatchValidator.validate(null));
    }

    @Test
    void rootPathReturnsIsRootPath() {
        Path p = Path.of("/");
        assertEquals(FileValidationResult.IS_ROOT_PATH, FileToWatchValidator.validate(p));
    }

    @Test
    void nonExistingParentDir() {
        Path tmp = Path.of(System.getProperty("java.io.tmpdir"), "nonexistent-dir-for-test", "nofile.txt");
        FileValidationResult r = FileToWatchValidator.validate(tmp);
        assertEquals(FileValidationResult.PARENT_DIR_NOT_FOUND, r);
    }

    @Test
    void parentNotADirectory() throws Exception {
        Path tmpFile = Files.createTempFile("testfile", ".txt");
        try {
            Path p = tmpFile.resolve("child");
            assertEquals(FileValidationResult.PARENT_NOT_A_DIRECTORY, FileToWatchValidator.validate(p));
        } finally {
            Files.deleteIfExists(tmpFile);
        }
    }

    @Test
    void fileNotFound() {
        Path tmp = Path.of(System.getProperty("java.io.tmpdir"), "nonexistent-file-for-test.txt");
        FileValidationResult r = FileToWatchValidator.validate(tmp);
        assertEquals(FileValidationResult.FILE_NOT_FOUND, r);
    }

    @Test
    void notARegularFile() throws Exception {
        Path dir = Files.createTempDirectory("testdir");
        try {
            Path p = dir.resolve("subdir");
            Files.createDirectory(p);
            FileValidationResult r = FileToWatchValidator.validate(p);
            assertEquals(FileValidationResult.NOT_A_REGULAR_FILE, r);
        } finally {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> { try { Files.deleteIfExists(path); } catch (Exception ignore) {} });
        }
    }
}

