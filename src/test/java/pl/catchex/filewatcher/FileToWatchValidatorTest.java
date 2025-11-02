package pl.catchex.filewatcher;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileToWatchValidatorTest {

    @Test
    void nullPathReturnsPathNull() {
        assertEquals(FileValidationResult.PATH_NULL, FileToWatchValidator.validate(null));
    }

    @Test
    void rootPathReturnsIsRootPath() {
        Path root = Path.of("C:");
        // On Windows, Path.of("C:") may not be considered root path in all contexts,
        // but we can emulate a path with no parent by using Path.of("/") on unix-like systems.
        // To keep tests cross-platform, create a path with no parent via Path.of("/tmp").getRoot().resolve("");
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
            Path child = tmpFile.resolveSibling("child");
            // child has parent tmpFile.getParent() which is directory, so to simulate parent not directory,
            // we use the temp file itself as parent by constructing a path like tmpFile.toAbsolutePath().resolve("child")
            Path p = tmpFile.resolve("child");
            // But Path#getParent will return tmpFile; tmpFile is a file, so validator should return PARENT_NOT_A_DIRECTORY
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
            // create a 'file' that is actually a directory
            Path p = dir.resolve("subdir");
            Files.createDirectory(p);
            FileValidationResult r = FileToWatchValidator.validate(p);
            assertEquals(FileValidationResult.NOT_A_REGULAR_FILE, r);
        } finally {
            Files.walk(dir)
                    .sorted((a,b) -> b.compareTo(a))
                    .forEach(path -> { try { Files.deleteIfExists(path); } catch (Exception ignore) {} });
        }
    }
}

