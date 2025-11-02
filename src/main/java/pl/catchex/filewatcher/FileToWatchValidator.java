package pl.catchex.filewatcher;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileToWatchValidator {

    private FileToWatchValidator() {}

    /**
     * Validate that a Path points to an existing regular file and has a valid parent directory.
     *
     * @param fileToWatch path to validate
     * @return FileValidationResult representing validation outcome
     */
    public static FileValidationResult validate(Path fileToWatch) {

        if (fileToWatch == null) {
            return FileValidationResult.PATH_NULL;
        }

        Path dirToWatch = fileToWatch.getParent();

        if (dirToWatch == null) {
            return FileValidationResult.IS_ROOT_PATH;
        }

        if (!Files.exists(dirToWatch)) {
            return FileValidationResult.PARENT_DIR_NOT_FOUND;
        }

        if (!Files.isDirectory(dirToWatch)) {
            return FileValidationResult.PARENT_NOT_A_DIRECTORY;
        }

        if (!Files.exists(fileToWatch)) {
            return FileValidationResult.FILE_NOT_FOUND;
        }

        if (!Files.isRegularFile(fileToWatch)) {
            return FileValidationResult.NOT_A_REGULAR_FILE;
        }

        return FileValidationResult.SUCCESS;
    }
}
