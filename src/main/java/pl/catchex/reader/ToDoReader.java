package pl.catchex.reader;

import pl.catchex.model.ToDoItem;
import pl.catchex.reader.lineparser.ToDoLineParserDispatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Component responsible for reading {@link ToDoItem} instances from a text file and
 * converting each text line into a {@link ToDoItem} using the provided
 * {@link ToDoLineParserDispatcher}.
 */
public class ToDoReader {

    private final ToDoLineParserDispatcher toDoLineParserDispatcher;

    private final Path todoListPath;

    /**
     * Create a new ToDoReader.
     *
     * @param toDoLineParserDispatcher dispatcher used to parse individual lines
     * @param todoListPath             path to the todo list file //NOSONAR
     */
    public ToDoReader(ToDoLineParserDispatcher toDoLineParserDispatcher, Path todoListPath){
        this.toDoLineParserDispatcher = toDoLineParserDispatcher;
        this.todoListPath = todoListPath;
    }

    /**
     * Read all {@link ToDoItem} instances from the configured file.
     *
     * @return list of parsed {@link ToDoItem} instances (empty list if no valid lines)
     * @throws IOException when reading the file fails
     */
    public List<ToDoItem> read() throws IOException {
        return read(this.todoListPath);
    }

    // --- private implementation helpers ---

    private List<ToDoItem> read(Path path) throws IOException {
        List<String> lines = readAllLines(path);
        return parseLines(lines);
    }

    private List<String> readAllLines(Path path) throws IOException {
        // keep using Files.readAllLines for simplicity; centralised here for easier testing/refactor
        return Files.readAllLines(path);
    }

    private List<ToDoItem> parseLines(List<String> lines) {
        List<ToDoItem> todos = new ArrayList<>();
        for (String line : lines) {
            toDoLineParserDispatcher.parse(line).ifPresent(todos::add);
        }
        return todos;
    }

}
