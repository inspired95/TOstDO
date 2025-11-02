package pl.catchex.filewatcher;

public enum FileValidationResult {

    SUCCESS("Validation successful."),

    // Error cases
    PATH_NULL("File path is null."),
    IS_ROOT_PATH("Cannot watch a file in the root directory (no parent directory)."),
    PARENT_DIR_NOT_FOUND("Parent directory does not exist."),
    PARENT_NOT_A_DIRECTORY("Parent path is not a directory."),
    FILE_NOT_FOUND("File does not exist."),
    NOT_A_REGULAR_FILE("Path is not a regular file.");

    private final String message;

    FileValidationResult(String message) {
        this.message = message;
    }

    /**
     * Returns a human-readable description of the validation result.
     * @return The success or error message.
     */
    public String getMessage() {
        return message;
    }

    public boolean failed() {
        return this != SUCCESS;
    }
}
